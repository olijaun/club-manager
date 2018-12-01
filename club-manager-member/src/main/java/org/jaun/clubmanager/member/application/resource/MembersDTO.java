package org.jaun.clubmanager.member.application.resource;

import java.util.ArrayList;

public class MembersDTO {

    private String subscriptionPeriodIdFilter;

    private final ArrayList<MemberDTO> members = new ArrayList<>();

    public ArrayList<MemberDTO> getMembers() {
        return members;
    }

    public void setSubscriptionPeriodIdFilter(String subscriptionPeriodIdFilter) {
        this.subscriptionPeriodIdFilter = subscriptionPeriodIdFilter;
    }

    public String getSubscriptionPeriodIdFilter() {
        return subscriptionPeriodIdFilter;
    }
}
