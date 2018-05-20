package org.jaun.clubmanager.invoice.domain.model.bankaccount;

import static org.junit.Assert.*;

import org.iban4j.Iban;
import org.junit.Test;

public class BankAccountNumberTest {
    @Test
    public void parseIban() throws Exception {
        BankAccountNumber bankAccountNumber = BankAccountNumber.parseIban("DE89370400440532013000");

        System.out.println(Iban.valueOf("DE89370400440532013000"));
    }

}