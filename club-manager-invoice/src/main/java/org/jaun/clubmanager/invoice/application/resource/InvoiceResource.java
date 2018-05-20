package org.jaun.clubmanager.invoice.application.resource;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.invoice.domain.model.bankaccount.BankAccountNumber;
import org.jaun.clubmanager.invoice.domain.model.invoice.Invoice;
import org.jaun.clubmanager.invoice.domain.model.invoice.InvoiceId;
import org.jaun.clubmanager.invoice.domain.model.invoice.InvoiceRepository;
import org.jaun.clubmanager.invoice.domain.model.invoice.PaymentId;
import org.jaun.clubmanager.invoice.domain.model.payer.PayerId;
import org.jaun.clubmanager.invoice.domain.model.recipient.RecipientId;
import org.jaun.clubmanager.invoice.infra.projection.HazelcastInvoiceProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/invoices")
public class InvoiceResource {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private HazelcastInvoiceProjection projection;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Response createInvoice(@PathParam("id") String invoiceIdAsString, CreateInvoiceDTO createInvoiceDTO) {

        Invoice invoice = new Invoice(new InvoiceId(invoiceIdAsString), new RecipientId(createInvoiceDTO.getRecipientId()),
                createInvoiceDTO.getAmount(), InvoiceConverter.toCurrency(createInvoiceDTO.getCurrency()),
                BankAccountNumber.parseIban(createInvoiceDTO.getBeneficiaryBankAccount()), createInvoiceDTO.getComment());

        try {
            invoiceRepository.save(invoice);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getInvoices() {

        Collection<InvoiceDTO> allInvoices = projection.getAllInvoices();

        InvoicesDTO invoicesDTO = new InvoicesDTO();
        invoicesDTO.setInvoices(allInvoices);

        return Response.ok(invoicesDTO).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getInvoice(@PathParam("id") String invoiceIdAsString) {

        InvoiceDTO invoice = projection.getInvoice(new InvoiceId(invoiceIdAsString));

        return Response.ok(invoice).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{invoice-id}/payments/{id}")
    public Response bookPayment(@PathParam("id") String paymentIdAsString, @PathParam("invoice-id") String invoiceIdAsString,
            CreatePaymentDTO createPaymentDTO) {

        Invoice invoice = invoiceRepository.get(new InvoiceId(invoiceIdAsString));

        if (invoice == null) {
            throw new NotFoundException("could not find invoice " + invoiceIdAsString);
        }

        invoice.bookPayment(new PaymentId(paymentIdAsString), new PayerId(createPaymentDTO.getPayerId()),
                createPaymentDTO.getReceivedDate(), createPaymentDTO.getAmount());

        try {
            invoiceRepository.save(invoice);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{invoice-id}/payments")
    public Response getPayments(@PathParam("invoice-id") String invoiceIdAsString) {

        Collection<PaymentDTO> payments = projection.getPayments(new InvoiceId(invoiceIdAsString));

        PaymentsDTO paymentsDTO = new PaymentsDTO();
        paymentsDTO.setPayments(payments);

        return Response.ok(payments).build();
    }
}
