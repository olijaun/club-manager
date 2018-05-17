package org.jaun.clubmanager.member.application.resource;

public class SubscriptionTypeDTO extends CreateSubscriptionTypeDTO {
    private String id;
    private String subscriptionPeriodId;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getSubscriptionPeriodId() {
        return subscriptionPeriodId;
    }

    public void setSubscriptionPeriodId(String subscriptionPeriodId) {
        this.subscriptionPeriodId = subscriptionPeriodId;
    }
}
