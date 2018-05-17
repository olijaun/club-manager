package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import java.util.Collection;

import org.jaun.clubmanager.member.domain.model.member.MemberId;

import com.google.common.collect.ImmutableList;

public class SubscriptionRequest {

    private final SubscriptionPeriodId subscriptionPeriodId;
    private final SubscriptionTypeId subscriptionTypeId;
    private final Collection<MemberId> additionalSubscriberIds;

    /**
     * this constructor is intentionally package protected so that only the SubscriptionPeriod can create it
     */
    SubscriptionRequest(SubscriptionPeriodId subscriptionPeriodId, SubscriptionTypeId subscriptionTypeId,
            Collection<MemberId> additionalSubscriberIds) {
        this.subscriptionPeriodId = subscriptionPeriodId;
        this.subscriptionTypeId = subscriptionTypeId;
        this.additionalSubscriberIds = ImmutableList.copyOf(additionalSubscriberIds);
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
