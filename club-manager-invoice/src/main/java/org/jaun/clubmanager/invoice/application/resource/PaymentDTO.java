package org.jaun.clubmanager.invoice.application.resource;

public class PaymentDTO extends CreatePaymentDTO {

    private String paymentId;

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
}
