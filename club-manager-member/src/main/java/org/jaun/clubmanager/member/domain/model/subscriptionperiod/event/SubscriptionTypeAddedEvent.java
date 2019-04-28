package org.jaun.clubmanager.member.domain.model.subscriptionperiod.event;

import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.Money;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;

import static java.util.Objects.requireNonNull;

public class SubscriptionTypeAddedEvent extends SubscriptionPeriodEvent {

    private final SubscriptionPeriodId subscriptionPeriodId;
    private final SubscriptionTypeId subscriptionTypeId;
    private final MembershipTypeId membershipTypeId;
    private final String name;
    private final Money price;
    private final int maxSubscribers;

    public SubscriptionTypeAddedEvent(SubscriptionPeriodId subscriptionPeriodId, SubscriptionTypeId subscriptionTypeId,
                                      MembershipTypeId membershipTypeId, String name, Money price, int maxSubscribers) {

        this.subscriptionPeriodId = requireNonNull(subscriptionPeriodId);
        this.subscriptionTypeId = requireNonNull(subscriptionTypeId);
        this.membershipTypeId = requireNonNull(membershipTypeId);
        this.name = requireNonNull(name);
        this.price = price;
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

    public Money getPrice() {
        return price;
    }

    public int getMaxSubscribers() {
        return maxSubscribers;
    }
}
