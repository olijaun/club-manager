package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;

import java.util.Currency;

class SubscriptionTypeFixture {

    public static SubscriptionType.Builder subscriptionType() {
        SubscriptionTypeId subscriptionTypeId = new SubscriptionTypeId("subTypeId1");
        MembershipTypeId membershipTypeId = new MembershipTypeId("memTyoeId1");
        Money price = new Money(60, Currency.getInstance("CHF"));
        int maxSubscribers = 1;
        String name = "bla";

        return SubscriptionType.builder()
                .subscriptionTypeId(subscriptionTypeId)
                .membershipTypeId(membershipTypeId)
                .price(price)
                .maxSubscribers(maxSubscribers)
                .name(name);
    }
}