package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import com.google.common.base.Preconditions;
import org.jaun.clubmanager.domain.model.commons.ValueObject;

import java.math.BigDecimal;
import java.util.Currency;

import static java.util.Objects.requireNonNull;

public class Money extends ValueObject {

    private long amount;
    private Currency currency;

    public Money(double amount, Currency currency) {
        this.currency = requireNonNull(currency);
        this.amount = Math.round(amount * centFactor());
    }

    public Money(long amount, Currency currency) {
        this.currency = requireNonNull(currency);
        this.amount = amount * centFactor();
    }

    private static final int[] cents = new int[]{1, 10, 100, 1000};

    private int centFactor() {
        return cents[currency.getDefaultFractionDigits()];
    }

    public BigDecimal amount() {
        return BigDecimal.valueOf(amount, currency.getDefaultFractionDigits());
    }

    public Currency currency() {
        return currency;
    }

    public static Money dollars(double amount) {
        return new Money(amount, Currency.getInstance("USD"));
    }

    public static Money swissFrancs(double amount) {
        return new Money(amount, Currency.getInstance("CHF"));
    }

    private void assertSameCurrencyAs(Money arg) {
        Preconditions.checkArgument(currency.equals(arg.currency), "money math mismatch");
    }

    public int compareTo(Object other) {
        return compareTo((Money) other);
    }

    public int compareTo(Money other) {
        assertSameCurrencyAs(other);
        if (amount < other.amount) return -1;
        else if (amount == other.amount) return 0;
        else return 1;
    }
}
