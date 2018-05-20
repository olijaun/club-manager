package org.jaun.clubmanager.invoice.application.resource;

import java.io.Serializable;

public class CreateInvoiceDTO implements Serializable {

    private String recipientId;
    private String comment;
    private Double amount;
    private String currency;
    private String beneficiaryBankAccount;

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getBeneficiaryBankAccount() {
        return beneficiaryBankAccount;
    }

    public void setBeneficiaryBankAccount(String beneficiaryBankAccount) {
        this.beneficiaryBankAccount = beneficiaryBankAccount;
    }
}
