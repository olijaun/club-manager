package org.jaun.clubmanager.member.application.resource;

import java.util.ArrayList;
import java.util.Collection;

public class CreateMemberDTO extends DTO {

    private Collection<SubscriptionDTO> subscriptions = new ArrayList<>();

    public Collection<SubscriptionDTO> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Collection<SubscriptionDTO> subscriptions) {
        if(subscriptions == null) {
            return;
        }
        this.subscriptions = subscriptions;
    }
}
