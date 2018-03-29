package org.jaun.clubmanager.member.domain.model.membership;

import java.util.Currency;

import org.jaun.clubmanager.domain.model.commons.Entity;

public class SubscriptionDefinition extends Entity<SubscriptionDefinitionId> {

    private final SubscriptionDefinitionId subscriptionDefinitionId;
    private final MembershipTypeId membershipTypeId; // e.g. Gönner, Normal, Passiv
    private final String name;
    private final double amount;
    private final Currency currency;
    private final int maxSubscribers;

    public SubscriptionDefinition(SubscriptionDefinitionId subscriptionDefinitionId, MembershipTypeId membershipTypeId, String name,
            double amount, Currency currency, int maxSubscribers) {
        this.subscriptionDefinitionId = subscriptionDefinitionId;
        this.membershipTypeId = membershipTypeId;
        this.name = name;
        this.amount = amount;
        this.currency = currency;
        this.maxSubscribers = maxSubscribers;
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

    public int getMaxSubscribers() {
        return maxSubscribers;
    }
}
