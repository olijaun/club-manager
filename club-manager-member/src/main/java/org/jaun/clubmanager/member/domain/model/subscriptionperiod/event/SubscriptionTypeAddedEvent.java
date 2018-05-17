package org.jaun.clubmanager.member.domain.model.subscriptionperiod.event;

import static java.util.Objects.requireNonNull;

import java.util.Currency;

import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;

public class SubscriptionTypeAddedEvent extends SubscriptionPeriodEvent {

    private final SubscriptionPeriodId subscriptionPeriodId;
    private final SubscriptionTypeId subscriptionTypeId;
    private final MembershipTypeId membershipTypeId;
    private final String name;
    private final double amount;
    private final Currency currency;
    private final int maxSubscribers;

    public SubscriptionTypeAddedEvent(SubscriptionPeriodId subscriptionPeriodId, SubscriptionTypeId subscriptionTypeId,
            MembershipTypeId membershipTypeId, String name, double amount, Currency currency, int maxSubscribers) {

        this.subscriptionPeriodId = requireNonNull(subscriptionPeriodId);
        this.subscriptionTypeId = requireNonNull(subscriptionTypeId);
        this.membershipTypeId = requireNonNull(membershipTypeId);
        this.name = requireNonNull(name);
        this.amount = amount;
        this.currency = requireNonNull(currency);
        this.maxSubscribers = maxSubscribers;
    }

    public SubscriptionPeriodId getSubscriptionPeriodId() {
        return subscriptionPeriodId;
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

    public int getMaxSubscribers() {
        return maxSubscribers;
    }
}
