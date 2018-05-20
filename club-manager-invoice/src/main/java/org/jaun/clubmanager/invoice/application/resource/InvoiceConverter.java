package org.jaun.clubmanager.invoice.application.resource;

import java.util.Currency;

public class InvoiceConverter {

    public static Currency toCurrency(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }

}
