package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class CreateMembershipDTO implements Serializable {

    private String subscriberId;
    private String membershipPeriodId;
    private String subscriptionOptionId;

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

    public String getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public void setMembershipPeriodId(String membershipPeriodId) {
        this.membershipPeriodId = membershipPeriodId;
    }

    public String getSubscriptionOptionId() {
        return subscriptionOptionId;
    }

    public void setSubscriptionOptionId(String subscriptionOptionId) {
        this.subscriptionOptionId = subscriptionOptionId;
    }
}
