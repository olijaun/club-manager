package org.jaun.clubmanager.member.domain.model.membership;

import java.util.Currency;

import org.jaun.clubmanager.domain.model.commons.Entity;

public class SubscriptionOption extends Entity<SubscriptionOptionId> {

    private final SubscriptionOptionId subscriptionOptionId;
    private final MembershipTypeId membershipTypeId; // e.g. Gönner, Normal, Passiv
    private final String name;
    private final double amount;
    private final Currency currency;
    private final int maxSubscribers;

    public SubscriptionOption(SubscriptionOptionId subscriptionOptionId, MembershipTypeId membershipTypeId, String name,
            double amount, Currency currency, int maxSubscribers) {
        this.subscriptionOptionId = subscriptionOptionId;
        this.membershipTypeId = membershipTypeId;
        this.name = name;
        this.amount = amount;
        this.currency = currency;
        this.maxSubscribers = maxSubscribers;
    }

    public SubscriptionOptionId getSubscriptionOptionId() {
        return subscriptionOptionId;
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

    public SubscriptionOptionId getId() {
        return subscriptionOptionId;
    }

    public int getMaxSubscribers() {
        return maxSubscribers;
    }
}
