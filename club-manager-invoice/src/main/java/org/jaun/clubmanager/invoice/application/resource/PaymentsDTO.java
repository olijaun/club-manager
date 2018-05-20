package org.jaun.clubmanager.invoice.application.resource;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

public class PaymentsDTO implements Serializable {

    private Collection<PaymentDTO> payments = Collections.emptyList();

    public Collection<PaymentDTO> getPayments() {
        return payments;
    }

    public void setPayments(Collection<PaymentDTO> payments) {
        if (payments != null) {
            this.payments = payments;
        }
    }
}
