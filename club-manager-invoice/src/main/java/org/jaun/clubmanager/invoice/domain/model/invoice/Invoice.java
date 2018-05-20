package org.jaun.clubmanager.invoice.domain.model.invoice;

import java.util.Currency;

import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.invoice.domain.model.bankaccount.BankAccountNumber;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.InvoiceCreatedEvent;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.InvoiceEvent;
import org.jaun.clubmanager.invoice.domain.model.recipient.RecipientId;

public class Invoice extends EventSourcingAggregate<InvoiceId, InvoiceEvent> {
    private InvoiceId id;
    private RecipientId recipientId;
    private Double amount;
    private Currency currency;
    private BankAccountNumber beneficiaryBankAccountNumber;

    public Invoice(InvoiceId id, RecipientId recipientId, Double amount, Currency currency,
            BankAccountNumber beneficiaryBankAccountNumber) {

        apply(new InvoiceCreatedEvent(id, recipientId, amount, currency, beneficiaryBankAccountNumber));
    }

    public Invoice(EventStream<InvoiceEvent> eventStream) {
        replayEvents(eventStream);
    }

    private void mutate(InvoiceCreatedEvent event) {
        this.id = event.getInvoiceId();
        this.recipientId = event.getRecipientId();
        this.amount = event.getAmount();
        this.currency = event.getCurrency();
        this.beneficiaryBankAccountNumber = event.getBeneficiaryBankAccountNumber();
    }

    @Override
    protected void mutate(InvoiceEvent event) {
        if (event instanceof InvoiceCreatedEvent) {
            mutate((InvoiceCreatedEvent) event);
        }
    }

    @Override
    public InvoiceId getId() {
        return id;
    }
}
