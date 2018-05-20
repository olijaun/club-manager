package org.jaun.clubmanager.invoice.domain.model.invoice.event;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

import org.jaun.clubmanager.invoice.domain.model.invoice.InvoiceId;
import org.jaun.clubmanager.invoice.domain.model.invoice.PaymentId;
import org.jaun.clubmanager.invoice.domain.model.payer.PayerId;

public class PaymentBookedEvent extends InvoiceEvent {

    private final PayerId payerId;
    private final InvoiceId invoiceId;
    private final PaymentId paymentId;
    private final LocalDate receivedDate;
    private final Double amount;

    public PaymentBookedEvent(InvoiceId invoiceId, PayerId payerId, PaymentId paymentId, LocalDate receivedDate, Double amount) {
        this.invoiceId = requireNonNull(invoiceId);
        this.payerId = payerId;
        this.paymentId = requireNonNull(paymentId);
        this.receivedDate = requireNonNull(receivedDate);
        this.amount = requireNonNull(amount);
    }

    public InvoiceId getInvoiceId() {
        return invoiceId;
    }

    public PaymentId getPaymentId() {
        return paymentId;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public Double getAmount() {
        return amount;
    }

    public PayerId getPayerId() {
        return payerId;
    }
}
