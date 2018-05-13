package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class SubscriptionOptionDTO implements Serializable {
    private String id;
    private String membershipPeriodId;
    private String membershipTypeId;
    private String name;
    private double amount;
    private String currency;
    private int maxSubscribers;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public SubscriptionOptionDTO setMembershipPeriodId(String membershipPeriodId) {
        this.membershipPeriodId = membershipPeriodId;
        return this;
    }

    public String getMembershipTypeId() {
        return membershipTypeId;
    }

    public SubscriptionOptionDTO setMembershipTypeId(String membershipTypeId) {
        this.membershipTypeId = membershipTypeId;
        return this;
    }

    public String getName() {
        return name;
    }

    public SubscriptionOptionDTO setName(String name) {
        this.name = name;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public SubscriptionOptionDTO setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public SubscriptionOptionDTO setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public int getMaxSubscribers() {
        return maxSubscribers;
    }

    public SubscriptionOptionDTO setMaxSubscribers(int maxSubscribers) {
        this.maxSubscribers = maxSubscribers;
        return this;
    }
}
