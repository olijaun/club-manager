package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class CreateMembershipDTO implements Serializable {

    private String membershipId;
    private String subscriberId;
    private String membershipPeriodId;
    private String subscriptionDefinitionId;

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

    public String getSubscriptionDefinitionId() {
        return subscriptionDefinitionId;
    }

    public CreateMembershipDTO setSubscriptionDefinitionId(String subscriptionDefinitionId) {
        this.subscriptionDefinitionId = subscriptionDefinitionId;
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
