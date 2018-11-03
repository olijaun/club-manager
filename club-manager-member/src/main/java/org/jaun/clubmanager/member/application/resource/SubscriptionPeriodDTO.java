package org.jaun.clubmanager.member.application.resource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubscriptionPeriodDTO extends CreateSubscriptionPeriodDTO {

    private String id;
    private Collection<SubscriptionTypeDTO> subscriptionTypes = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Collection<SubscriptionTypeDTO> getSubscriptionTypes() {
        return subscriptionTypes;
    }

    public void setSubscriptionTypes(Collection<SubscriptionTypeDTO> subscriptionTypes) {
        if (subscriptionTypes == null) {
            return;
        }
        this.subscriptionTypes = subscriptionTypes;
    }
}
