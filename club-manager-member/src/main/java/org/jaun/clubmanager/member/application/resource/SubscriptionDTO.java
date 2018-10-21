package org.jaun.clubmanager.member.application.resource;

public class SubscriptionDTO extends CreateSubscriptionDTO {

    private String id;
    private String memberId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
