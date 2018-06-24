package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class CreateSubscriptionDTO extends DTO {

    private String subscriberId;
    private String subscriptionPeriodId;
    private String subscriptionTypeId;

    public String getSubscriberId() {
        return subscriberId;
    }

    public void setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
    }

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
