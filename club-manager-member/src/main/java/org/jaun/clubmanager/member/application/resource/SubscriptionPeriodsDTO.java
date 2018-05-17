package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SubscriptionPeriodsDTO implements Serializable {
    private ArrayList<SubscriptionPeriodDTO> subscriptionPeriods = new ArrayList<>();

    public List<SubscriptionPeriodDTO> getSubscriptionPeriods() {
        return subscriptionPeriods;
    }

    public void setSubscriptionPeriods(Collection<SubscriptionPeriodDTO> subscriptionPeriodsDTOS) {
        if (subscriptionPeriodsDTOS != null) {
            this.subscriptionPeriods = new ArrayList<>(subscriptionPeriodsDTOS);
        }
    }
}
