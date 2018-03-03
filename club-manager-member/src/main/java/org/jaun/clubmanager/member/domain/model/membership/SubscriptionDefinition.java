package org.jaun.clubmanager.member.domain.model.membership;

import org.jaun.clubmanager.domain.model.commons.Entity;

import java.util.Currency;

public class SubscriptionDefinition extends Entity<SubscriptionDefinitionId> {

    private final SubscriptionDefinitionId subscriptionDefinitionId;
    private final MembershipTypeId membershipTypeId; // e.g. Gönner, Normal, Passiv
    private final String name;
    private final double amount;
    private final Currency currency;

    public SubscriptionDefinition(SubscriptionDefinitionId subscriptionDefinitionId, MembershipTypeId membershipTypeId, String name, double amount, Currency currency) {
        this.subscriptionDefinitionId = subscriptionDefinitionId;
        this.membershipTypeId = membershipTypeId;
        this.name = name;
        this.amount = amount;
        this.currency = currency;
    }

    public SubscriptionDefinitionId getSubscriptionDefinitionId() {
        return subscriptionDefinitionId;
    }

    public MembershipTypeId getMembershipTypeId() {
        return membershipTypeId;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public SubscriptionDefinitionId getId() {
        return subscriptionDefinitionId;
    }
}
