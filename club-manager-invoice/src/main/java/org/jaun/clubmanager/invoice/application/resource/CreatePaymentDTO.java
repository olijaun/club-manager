package org.jaun.clubmanager.invoice.application.resource;

import java.io.Serializable;
import java.time.LocalDate;

public class CreatePaymentDTO implements Serializable {

    private String payerId;
    private LocalDate receivedDate;
    private Double amount;

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public LocalDate getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(LocalDate receivedDate) {
        this.receivedDate = receivedDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
