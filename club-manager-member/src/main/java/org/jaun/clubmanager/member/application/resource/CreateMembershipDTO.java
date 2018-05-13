package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class CreateMembershipDTO implements Serializable {

    private String membershipId;
    private String subscriberId;
    private String membershipPeriodId;
    private String subscriptionOptionId;

    public String getMembershipId() {
        return membershipId;
    }

    public CreateMembershipDTO setMembershipId(String membershipId) {
        this.membershipId = membershipId;
        return this;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public CreateMembershipDTO setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
        return this;
    }

    public String getSubscriptionOptionId() {
        return subscriptionOptionId;
    }

    public CreateMembershipDTO setSubscriptionOptionId(String subscriptionOptionId) {
        this.subscriptionOptionId = subscriptionOptionId;
        return this;
    }

    public String getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public CreateMembershipDTO setMembershipPeriodId(String membershipPeriodId) {
        this.membershipPeriodId = membershipPeriodId;
        return this;
    }
}
