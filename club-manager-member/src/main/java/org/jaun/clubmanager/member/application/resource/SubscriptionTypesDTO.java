package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubscriptionTypesDTO implements Serializable {
    private ArrayList<SubscriptionTypeDTO> subscriptionTypes = new ArrayList<>();

    public List<SubscriptionTypeDTO> getSubscriptionTypes() {
        return subscriptionTypes;
    }

    public void setSubscriptionTypes(Collection<SubscriptionTypeDTO> subscriptionTypesDTOS) {
        if (subscriptionTypes != null) {
            this.subscriptionTypes = new ArrayList<>(subscriptionTypesDTOS);
        }
    }
}
