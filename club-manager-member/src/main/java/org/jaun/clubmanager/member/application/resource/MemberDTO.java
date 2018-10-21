package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class MemberDTO implements Serializable {

    private String id;
    private String firstName;
    private String lastNameOrCompanyName;
    private Collection<SubscriptionDTO> subscriptions = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastNameOrCompanyName() {
        return lastNameOrCompanyName;
    }

    public void setLastNameOrCompanyName(String lastNameOrCompanyName) {
        this.lastNameOrCompanyName = lastNameOrCompanyName;
    }

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
