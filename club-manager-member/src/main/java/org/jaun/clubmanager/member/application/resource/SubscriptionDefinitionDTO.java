package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class SubscriptionDefinitionDTO implements Serializable {
    private String membershipPeriodId;
    private String membershipTypeId;
    private String name;
    private double amount;
    private String currency;
    private int maxSubscribers;

    public String getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public SubscriptionDefinitionDTO setMembershipPeriodId(String membershipPeriodId) {
        this.membershipPeriodId = membershipPeriodId;
        return this;
    }

    public String getMembershipTypeId() {
        return membershipTypeId;
    }

    public SubscriptionDefinitionDTO setMembershipTypeId(String membershipTypeId) {
        this.membershipTypeId = membershipTypeId;
        return this;
    }

    public String getName() {
        return name;
    }

    public SubscriptionDefinitionDTO setName(String name) {
        this.name = name;
        return this;
    }

    public double getAmount() {
        return amount;
    }

    public SubscriptionDefinitionDTO setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public SubscriptionDefinitionDTO setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public int getMaxSubscribers() {
        return maxSubscribers;
    }

    public SubscriptionDefinitionDTO setMaxSubscribers(int maxSubscribers) {
        this.maxSubscribers = maxSubscribers;
        return this;
    }
}
