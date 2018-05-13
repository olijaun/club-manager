package org.jaun.clubmanager.member.application.resource;

public class SubscriptionOptionDTO extends CreateSubscriptionOptionDTO {
    private String id;
    private String membershipPeriodId;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public CreateSubscriptionOptionDTO setMembershipPeriodId(String membershipPeriodId) {
        this.membershipPeriodId = membershipPeriodId;
        return this;
    }
}
