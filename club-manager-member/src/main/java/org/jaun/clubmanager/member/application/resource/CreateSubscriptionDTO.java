package org.jaun.clubmanager.member.application.resource;

public class CreateSubscriptionDTO extends DTO {

    private String subscriptionPeriodId;
    private String subscriptionTypeId;

    public String getSubscriptionPeriodId() {
        return subscriptionPeriodId;
    }

    public void setSubscriptionPeriodId(String subscriptionPeriodId) {
        this.subscriptionPeriodId = subscriptionPeriodId;
    }

    public String getSubscriptionTypeId() {
        return subscriptionTypeId;
    }

    public void setSubscriptionTypeId(String subscriptionTypeId) {
        this.subscriptionTypeId = subscriptionTypeId;
    }
}
