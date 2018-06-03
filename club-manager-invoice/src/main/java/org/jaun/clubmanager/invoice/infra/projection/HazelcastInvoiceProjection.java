package org.jaun.clubmanager.invoice.infra.projection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jaun.clubmanager.domain.model.commons.AbstractProjection;
import org.jaun.clubmanager.invoice.application.resource.InvoiceDTO;
import org.jaun.clubmanager.invoice.application.resource.PaymentDTO;
import org.jaun.clubmanager.invoice.domain.model.invoice.InvoiceId;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.InvoiceCreatedEvent;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.PaymentBookedEvent;
import org.jaun.clubmanager.invoice.infra.repository.InvoiceEventMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.msemys.esjc.EventStore;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

//@Service
public class HazelcastInvoiceProjection extends AbstractProjection {

    private final IMap<InvoiceId, InvoiceDTO> invoiceMap;
    private final IMap<InvoiceId, List<PaymentDTO>> paymentMap;

    public HazelcastInvoiceProjection(@Autowired EventStore eventStore, @Autowired HazelcastInstance hazelcastInstance) {
        super(eventStore, "$ce-invoice");

        registerMapping(InvoiceEventMapping.INVOICE_CREATED, (v, r) -> update(v, toObject(r, InvoiceCreatedEvent.class)));

        registerMapping(InvoiceEventMapping.PAYMENT_BOOKED, (v, r) -> update(v, toObject(r, PaymentBookedEvent.class)));

        invoiceMap = hazelcastInstance.getMap("invoices");
        paymentMap = hazelcastInstance.getMap("payments");
    }

    private void update(Long version, InvoiceCreatedEvent event) {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(event.getInvoiceId().getValue());
        invoiceDTO.setAmount(event.getAmount());
        invoiceDTO.setBeneficiaryBankAccount(event.getBeneficiaryBankAccountNumber().getValue());
        invoiceDTO.setComment(event.getComment().orElse(null));
        invoiceDTO.setCurrency(event.getCurrency().getCurrencyCode());
        invoiceDTO.setRecipientId(event.getRecipientId().getValue());
        invoiceMap.put(event.getInvoiceId(), invoiceDTO);
    }

    private void update(Long version, PaymentBookedEvent event) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setPaymentId(event.getPaymentId().getValue());
        paymentDTO.setAmount(event.getAmount());
        paymentDTO.setPayerId(event.getPayerId().getValue());
        paymentDTO.setReceivedDate(event.getReceivedDate());

        List<PaymentDTO> paymentDTOS = paymentMap.get(event.getInvoiceId());

        if (paymentDTOS == null) {
            paymentDTOS = new ArrayList<>();
        }

        paymentDTOS.add(paymentDTO);

        paymentMap.put(event.getInvoiceId(), paymentDTOS);
    }

    public InvoiceDTO getInvoice(InvoiceId invoiceId) {
        return invoiceMap.get(invoiceId);
    }

    public Collection<InvoiceDTO> getAllInvoices() {
        return invoiceMap.values();
    }

    public Collection<PaymentDTO> getPayments(InvoiceId invoiceId) {
        List<PaymentDTO> paymentDTOS = paymentMap.get(invoiceId);
        if (paymentDTOS == null) {
            return Collections.emptyList();
        }
        return paymentDTOS;
    }
}
