package org.jaun.clubmanager.invoice.domain.model.invoice;

import java.util.ArrayList;
import java.util.List;

public class Payments {

    private List<Payment> payments;

    public Payments() {
        this.payments = new ArrayList<>();
    }

    public Double sum() {
        return payments.stream().map(Payment::getAmount).mapToDouble(Double::doubleValue).sum();
    }

    public void addPayment(Payment payment) {
        this.payments.add(payment);
    }
}
