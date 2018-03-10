package org.jaun.clubmanager.member.application.resource;

public class MembershipDTO {

    private String membershipId;
    private String subscriberId;
    private String membershipPeriodId;
    private String subscriptionDefinitionId;

    public String getMembershipId() {
        return membershipId;
    }

    public MembershipDTO setMembershipId(String membershipId) {
        this.membershipId = membershipId;
        return this;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public MembershipDTO setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
        return this;
    }

    public String getSubscriptionDefinitionId() {
        return subscriptionDefinitionId;
    }

    public MembershipDTO setSubscriptionDefinitionId(String subscriptionDefinitionId) {
        this.subscriptionDefinitionId = subscriptionDefinitionId;
        return this;
    }

    public String getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public MembershipDTO setMembershipPeriodId(String membershipPeriodId) {
        this.membershipPeriodId = membershipPeriodId;
        return this;
    }
}
