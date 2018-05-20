package org.jaun.clubmanager.invoice.infra.projection;

import org.jaun.clubmanager.domain.model.commons.AbstractProjection;
import org.jaun.clubmanager.invoice.application.resource.InvoiceDTO;
import org.jaun.clubmanager.invoice.domain.model.invoice.InvoiceId;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.InvoiceCreatedEvent;
import org.jaun.clubmanager.invoice.infra.repository.InvoiceEventMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.msemys.esjc.EventStore;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Service
public class HazelcastInvoiceProjection extends AbstractProjection {

    private final IMap<InvoiceId, InvoiceDTO> invoiceMap;

    public HazelcastInvoiceProjection(@Autowired EventStore eventStore, @Autowired HazelcastInstance hazelcastInstance) {
        super(eventStore, "$ce-invoice");

        registerMapping(InvoiceEventMapping.INVOICE_CREATED, (v, r) -> update(v, toObject(r, InvoiceCreatedEvent.class)));

        invoiceMap = hazelcastInstance.getMap("invoices");
    }

    private void update(Long version, InvoiceCreatedEvent event) {
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setId(event.getInvoiceId().getValue());

        invoiceMap.put(event.getInvoiceId(), invoiceDTO);
    }

    public void getAllInvoices() {
        invoiceMap.values();
    }
}
