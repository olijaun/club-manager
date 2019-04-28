package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class CreateSubscriptionTypeDTO implements Serializable {

    private String membershipTypeId;
    private String name;
    private long amount;
    private String currency;
    private int maxSubscribers;

    public String getMembershipTypeId() {
        return membershipTypeId;
    }

    public void setMembershipTypeId(String membershipTypeId) {
        this.membershipTypeId = membershipTypeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getMaxSubscribers() {
        return maxSubscribers;
    }

    public void setMaxSubscribers(int maxSubscribers) {
        this.maxSubscribers = maxSubscribers;
    }
}
