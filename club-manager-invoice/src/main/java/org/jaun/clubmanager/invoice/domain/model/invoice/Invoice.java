package org.jaun.clubmanager.invoice.domain.model.invoice;

import java.time.LocalDate;
import java.util.Currency;

import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.invoice.domain.model.bankaccount.BankAccountNumber;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.InvoiceCreatedEvent;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.InvoiceEvent;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.PaymentBookedEvent;
import org.jaun.clubmanager.invoice.domain.model.payer.PayerId;
import org.jaun.clubmanager.invoice.domain.model.recipient.RecipientId;

public class Invoice extends EventSourcingAggregate<InvoiceId, InvoiceEvent> {
    private InvoiceId id;
    private RecipientId recipientId;
    private Double amount;
    private Currency currency;
    private BankAccountNumber beneficiaryBankAccountNumber;
    private final Payments payments = new Payments();

    public Invoice(InvoiceId id, RecipientId recipientId, Double amount, Currency currency,
            BankAccountNumber beneficiaryBankAccountNumber, String comment) {

        apply(new InvoiceCreatedEvent(id, recipientId, amount, currency, beneficiaryBankAccountNumber, comment));
    }

    public Invoice(EventStream<InvoiceEvent> eventStream) {
        replayEvents(eventStream);
    }

    public void bookPayment(PaymentId paymentId, PayerId payerId, LocalDate receivedDate, Double amount) {
        apply(new PaymentBookedEvent(id, payerId, paymentId, receivedDate, amount));
    }

    public boolean isPayed() {
        return payments.sum() >= amount;
    }

    private void mutate(InvoiceCreatedEvent event) {
        this.id = event.getInvoiceId();
        this.recipientId = event.getRecipientId();
        this.amount = event.getAmount();
        this.currency = event.getCurrency();
        this.beneficiaryBankAccountNumber = event.getBeneficiaryBankAccountNumber();
    }

    private void mutate(PaymentBookedEvent event) {
        Payment payment = new Payment(event.getPaymentId(), event.getPayerId(), event.getReceivedDate(), event.getAmount());
        payments.addPayment(payment);
    }

    @Override
    protected void mutate(InvoiceEvent event) {
        if (event instanceof InvoiceCreatedEvent) {
            mutate((InvoiceCreatedEvent) event);
        } else if (event instanceof PaymentBookedEvent) {
            mutate((PaymentBookedEvent) event);
        }
    }

    @Override
    public InvoiceId getId() {
        return id;
    }
}
