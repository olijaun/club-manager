package org.jaun.clubmanager.member.domain.model.membershipperiod.event;

import static java.util.Objects.requireNonNull;

import java.util.Currency;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.SubscriptionOptionId;
import org.jaun.clubmanager.member.infra.repository.MembershipPeriodEventMapping;

public class MembershipPeriodSubscriptionOptionAddedEvent extends MembershipPeriodEvent {

    private final MembershipPeriodId membershipPeriodId;
    private final SubscriptionOptionId subscriptionOptionId;
    private final MembershipTypeId membershipTypeId;
    private final String name;
    private final double amount;
    private final Currency currency;
    private final int maxSubscribers;

    public MembershipPeriodSubscriptionOptionAddedEvent(MembershipPeriodId membershipPeriodId,
            SubscriptionOptionId subscriptionOptionId, MembershipTypeId membershipTypeId, String name, double amount,
            Currency currency, int maxSubscribers) {

        this.membershipPeriodId = requireNonNull(membershipPeriodId);
        this.subscriptionOptionId = requireNonNull(subscriptionOptionId);
        this.membershipTypeId = requireNonNull(membershipTypeId);
        this.name = requireNonNull(name);
        this.amount = amount;
        this.currency = requireNonNull(currency);
        this.maxSubscribers = maxSubscribers;
    }

    public MembershipPeriodId getMembershipPeriodId() {
        return membershipPeriodId;
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

    public int getMaxSubscribers() {
        return maxSubscribers;
    }
}
