package org.jaun.clubmanager.member.domain.model.subscriptionperiod;


import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;

public class SubscriptionRequestFixture {

    public static SubscriptionRequest.Builder subscriptionRequest() {
        return SubscriptionRequest.builder()
                .subscriptionId(new SubscriptionId("subscriptionA"))
                .subscriptionPeriodId(new SubscriptionPeriodId("periodA"))
                .subscriptionTypeId(new SubscriptionTypeId("subTypeA"));
    }
}