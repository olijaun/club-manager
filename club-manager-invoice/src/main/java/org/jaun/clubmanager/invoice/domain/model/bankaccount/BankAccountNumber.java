package org.jaun.clubmanager.invoice.domain.model.bankaccount;

import org.iban4j.Iban;
import org.jaun.clubmanager.domain.model.commons.ValueObject;

public class BankAccountNumber extends ValueObject {
    private String value;

    private BankAccountNumber(String ibanNumber) {

    }

    public static BankAccountNumber parseIban(String ibanAsString) {
        return new BankAccountNumber(Iban.valueOf(ibanAsString).getAccountNumber());
    }

}