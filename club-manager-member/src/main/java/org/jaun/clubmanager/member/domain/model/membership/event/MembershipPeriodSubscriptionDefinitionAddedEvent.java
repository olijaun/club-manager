package org.jaun.clubmanager.member.domain.model.membership.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membership.SubscriptionDefinitionId;
import org.jaun.clubmanager.member.domain.model.membership.SubscriptionId;

import java.util.Collection;
import java.util.Currency;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class MembershipPeriodSubscriptionDefinitionAddedEvent extends DomainEvent<MembershipPeriodEventType> {

    private final MembershipPeriodId membershipPeriodId;
    private final SubscriptionDefinitionId subscriptionDefinitionId;
    private final MembershipTypeId membershipTypeId;
    private final String name;
    private final double amount;
    private final Currency currency;
    private final int maxSubscribers;

    public MembershipPeriodSubscriptionDefinitionAddedEvent(MembershipPeriodId membershipPeriodId, SubscriptionDefinitionId subscriptionDefinitionId, MembershipTypeId membershipTypeId, String name, double amount, Currency currency, int maxSubscribers) {

        super(MembershipPeriodEventType.SUBSCRIPTION_DEFINITION_ADDED);
        this.membershipPeriodId = requireNonNull(membershipPeriodId);
        this.subscriptionDefinitionId = requireNonNull(subscriptionDefinitionId);
        this.membershipTypeId = requireNonNull(membershipTypeId);
        this.name = requireNonNull(name);
        this.amount = amount;
        this.currency = requireNonNull(currency);
        this.maxSubscribers = maxSubscribers;
    }

    public MembershipPeriodId getMembershipPeriodId() {
        return membershipPeriodId;
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

    public int getMaxSubscribers() {
        return maxSubscribers;
    }
}
