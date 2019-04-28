package org.jaun.clubmanager.member.domain.model.subscriptionperiod;


import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.Subscription;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;

public class SubscriptionFixture {

    public static Subscription.Builder subscription() {
        return Subscription.builder()
                .id(new SubscriptionId("subscriptionA"))
                .subscriptionPeriodId(new SubscriptionPeriodId("periodA"))
                .subscriptionTypeId(new SubscriptionTypeId("subTypeA"))
                .memberId(new MemberId("memberA"));
    }
}