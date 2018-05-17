package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import java.util.Currency;

import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;

public class SubscriptionType extends Entity<SubscriptionTypeId> {

    private final SubscriptionTypeId subscriptionTypeId;
    private final MembershipTypeId membershipTypeId; // e.g. Gönner, Normal, Passiv
    private final String name;
    private final double amount;
    private final Currency currency;
    private final int maxSubscribers;

    public SubscriptionType(SubscriptionTypeId subscriptionTypeId, MembershipTypeId membershipTypeId, String name,
            double amount, Currency currency, int maxSubscribers) {
        this.subscriptionTypeId = subscriptionTypeId;
        this.membershipTypeId = membershipTypeId;
        this.name = name;
        this.amount = amount;
        this.currency = currency;
        this.maxSubscribers = maxSubscribers;
    }

    public SubscriptionTypeId getSubscriptionTypeId() {
        return subscriptionTypeId;
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

    public SubscriptionTypeId getId() {
        return subscriptionTypeId;
    }

    public int getMaxSubscribers() {
        return maxSubscribers;
    }
}
