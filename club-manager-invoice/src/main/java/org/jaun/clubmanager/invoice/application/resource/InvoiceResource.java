package org.jaun.clubmanager.invoice.application.resource;

import javax.ws.rs.Consumes;
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
import org.jaun.clubmanager.invoice.domain.model.recipient.RecipientId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/invoices")
public class InvoiceResource {

    @Autowired
    private InvoiceRepository invoiceRepository;

    //@Autowired
    //private HazelcastContactProjection projection;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Response createInvoice(@PathParam("id") String invoiceIdAsString, CreateInvoiceDTO createInvoiceDTO) {

        Invoice invoice = new Invoice(new InvoiceId(invoiceIdAsString), new RecipientId(createInvoiceDTO.getRecipientId()),
                createInvoiceDTO.getAmount(), InvoiceConverter.toCurrency(createInvoiceDTO.getCurrency()),
                BankAccountNumber.parseIban(createInvoiceDTO.getBeneficiaryBankAccount()));

        try {
            invoiceRepository.save(invoice);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.noContent().build();
    }

}
