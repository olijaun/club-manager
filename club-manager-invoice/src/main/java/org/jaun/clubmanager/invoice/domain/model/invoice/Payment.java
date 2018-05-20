package org.jaun.clubmanager.invoice.domain.model.invoice;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.invoice.domain.model.payer.PayerId;

public class Payment extends Entity<PaymentId> {

    private final PayerId payerId;
    private final LocalDate receivedDate;
    private final PaymentId paymentId;
    private final Double amount;

    public Payment(PaymentId paymentId, PayerId payerId, LocalDate receivedDate, Double amount) {
        this.paymentId = requireNonNull(paymentId);
        this.payerId = requireNonNull(payerId);
        this.amount = requireNonNull(amount);
        this.receivedDate = requireNonNull(receivedDate);
    }

    @Override
    public PaymentId getId() {
        return null;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public PaymentId getPaymentId() {
        return paymentId;
    }

    public Double getAmount() {
        return amount;
    }
}
