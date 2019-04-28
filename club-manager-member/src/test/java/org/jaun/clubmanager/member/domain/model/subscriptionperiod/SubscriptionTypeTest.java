package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Currency;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubscriptionTypeTest {

    @Test
    void create() {

        // prepare
        SubscriptionTypeId subscriptionTypeId = new SubscriptionTypeId("subTypeId1");
        MembershipTypeId membershipTypeId = new MembershipTypeId("memTyoeId1");
        double amount = 3.;
        Currency currency = Currency.getInstance("CHF");
        int maxSubscribers = 1;
        String name = "bla";

        // run
        SubscriptionType subscriptionType = SubscriptionType.builder()
                .subscriptionTypeId(subscriptionTypeId)
                .membershipTypeId(membershipTypeId)
                .amount(amount)
                .currency(currency)
                .maxSubscribers(maxSubscribers)
                .name(name)
                .build();

        // verify
        assertThat(subscriptionType.getId(), equalTo(subscriptionTypeId));
        assertThat(subscriptionType.getMaxSubscribers(), is(maxSubscribers));
        assertThat(subscriptionType.getCurrency(), equalTo(currency));
        assertThat(subscriptionType.getName(), equalTo(name));
        assertThat(subscriptionType.getMembershipTypeId(), equalTo(membershipTypeId));
        assertThat(subscriptionType.getSubscriptionTypeId(), equalTo(subscriptionTypeId));
    }

    @Test
    void create_null() {

        // run
        Executable e1 = () -> SubscriptionTypeFixture.subscriptionType().subscriptionTypeId(null).build();
        Executable e2 = () -> SubscriptionTypeFixture.subscriptionType().maxSubscribers(null).build();
        Executable e3 = () -> SubscriptionTypeFixture.subscriptionType().currency(null).build();
        Executable e4 = () -> SubscriptionTypeFixture.subscriptionType().amount(null).build();
        Executable e5 = () -> SubscriptionTypeFixture.subscriptionType().name(null).build();
        Executable e6 = () -> SubscriptionTypeFixture.subscriptionType().membershipTypeId(null).build();

        // verify
        assertThrows(NullPointerException.class, e1);
        assertThrows(NullPointerException.class, e2);
        assertThrows(NullPointerException.class, e3);
        assertThrows(NullPointerException.class, e4);
        assertThrows(NullPointerException.class, e5);
        assertThrows(NullPointerException.class, e6);
    }
}