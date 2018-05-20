package org.jaun.clubmanager.invoice.domain.model.invoice.event;

import static java.util.Objects.requireNonNull;

import java.util.Currency;
import java.util.Optional;

import org.jaun.clubmanager.invoice.domain.model.bankaccount.BankAccountNumber;
import org.jaun.clubmanager.invoice.domain.model.invoice.InvoiceId;
import org.jaun.clubmanager.invoice.domain.model.recipient.RecipientId;

public class InvoiceCreatedEvent extends InvoiceEvent {

    private final InvoiceId invoiceId;
    private final RecipientId recipientId;
    private final Double amount;
    private final Currency currency;
    private final BankAccountNumber beneficiaryBankAccountNumber;
    private final String comment;

    public InvoiceCreatedEvent(InvoiceId invoiceId, RecipientId recipientId, Double amount, Currency currency,
            BankAccountNumber beneficiaryBankAccountNumber, String comment) {

        this.invoiceId = requireNonNull(invoiceId);
        this.recipientId = requireNonNull(recipientId);
        this.amount = requireNonNull(amount);
        this.currency = requireNonNull(currency);
        this.beneficiaryBankAccountNumber = requireNonNull(beneficiaryBankAccountNumber);
        this.comment = comment;
    }

    public InvoiceId getInvoiceId() {
        return invoiceId;
    }

    public RecipientId getRecipientId() {
        return recipientId;
    }

    public Double getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public BankAccountNumber getBeneficiaryBankAccountNumber() {
        return beneficiaryBankAccountNumber;
    }

    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }
}
