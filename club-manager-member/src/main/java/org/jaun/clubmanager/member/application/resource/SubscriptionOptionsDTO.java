package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubscriptionOptionsDTO implements Serializable {
    private ArrayList<SubscriptionOptionDTO> subscriptionOptions = new ArrayList<>();

    public List<SubscriptionOptionDTO> getSubscriptionOptions() {
        return subscriptionOptions;
    }

    public void setSubscriptionOptions(Collection<SubscriptionOptionDTO> subscriptionOptionsDTOS) {
        if (subscriptionOptionsDTOS != null) {
            this.subscriptionOptions = new ArrayList<>(subscriptionOptionsDTOS);
        }
    }
}
