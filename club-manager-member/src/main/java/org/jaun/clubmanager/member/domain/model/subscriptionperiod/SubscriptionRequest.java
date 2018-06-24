package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;

import com.google.common.collect.ImmutableList;

public class SubscriptionRequest {

    private final SubscriptionId subscriptionId;
    private final SubscriptionPeriodId subscriptionPeriodId;
    private final SubscriptionTypeId subscriptionTypeId;
    private final Collection<MemberId> additionalSubscriberIds;

    /**
     * this constructor is intentionally package protected so that only the SubscriptionPeriod can create it
     */
    SubscriptionRequest(SubscriptionId subscriptionId, SubscriptionPeriodId subscriptionPeriodId,
            SubscriptionTypeId subscriptionTypeId, Collection<MemberId> additionalSubscriberIds) {
        this.subscriptionId = requireNonNull(subscriptionId);
        this.subscriptionPeriodId = requireNonNull(subscriptionPeriodId);
        this.subscriptionTypeId = requireNonNull(subscriptionTypeId);
        this.additionalSubscriberIds = ImmutableList.copyOf(additionalSubscriberIds);
    }

    public SubscriptionId getSubscriptionId() {
        return subscriptionId;
    }

    public SubscriptionPeriodId getSubscriptionPeriodId() {
        return subscriptionPeriodId;
    }

    public SubscriptionTypeId getSubscriptionTypeId() {
        return subscriptionTypeId;
    }

    public Collection<MemberId> getAdditionalSubscriberIds() {
        return additionalSubscriberIds;
    }
}
