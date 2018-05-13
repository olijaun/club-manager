package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class CreateSubscriptionOptionDTO implements Serializable {

    private String membershipTypeId;
    private String name;
    private double amount;
    private String currency;
    private int maxSubscribers;

    public String getMembershipTypeId() {
        return membershipTypeId;
    }

    public CreateSubscriptionOptionDTO setMembershipTypeId(String membershipTypeId) {
        this.membershipTypeId = membershipTypeId;
        return this;
    }

    public String getName() {
        return name;
    }

    public CreateSubscriptionOptionDTO setName(String name) {
        this.name = name;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public CreateSubscriptionOptionDTO setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public CreateSubscriptionOptionDTO setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public int getMaxSubscribers() {
        return maxSubscribers;
    }

    public CreateSubscriptionOptionDTO setMaxSubscribers(int maxSubscribers) {
        this.maxSubscribers = maxSubscribers;
        return this;
    }
}
