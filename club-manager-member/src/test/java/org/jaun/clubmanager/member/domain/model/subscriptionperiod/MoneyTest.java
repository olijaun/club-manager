package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class MoneyTest {

    @Test
    void construct_swissFrancs() {
        Money money = new Money(1L, Currency.getInstance("CHF"));

        assertThat(money.amount(), equalTo(BigDecimal.valueOf(100L, Currency.getInstance("CHF").getDefaultFractionDigits())));
        assertThat(money.currency(), equalTo(Currency.getInstance("CHF")));
    }

    @Test
    void swissFrancs() {
        Money money = Money.swissFrancs(1);

        assertThat(money.amount(), equalTo(BigDecimal.valueOf(100L, Currency.getInstance("CHF").getDefaultFractionDigits())));
        assertThat(money.currency(), equalTo(Currency.getInstance("CHF")));
    }

    @Test
    void dollars() {
        Money money = Money.dollars(1);

        assertThat(money.amount(), equalTo(BigDecimal.valueOf(100L, Currency.getInstance("USD").getDefaultFractionDigits())));
        assertThat(money.currency(), equalTo(Currency.getInstance("USD")));
    }
}