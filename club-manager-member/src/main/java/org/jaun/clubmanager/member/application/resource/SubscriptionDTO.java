package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class SubscriptionDTO extends CreateSubscriptionDTO {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
