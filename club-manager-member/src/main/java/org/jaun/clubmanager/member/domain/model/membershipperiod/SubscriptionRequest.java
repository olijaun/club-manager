package org.jaun.clubmanager.member.domain.model.membershipperiod;

import java.util.Collection;

import org.jaun.clubmanager.member.domain.model.member.MemberId;

import com.google.common.collect.ImmutableList;

public class SubscriptionRequest {

    private final MembershipPeriodId membershipPeriodId;
    private final SubscriptionOptionId subscriptionOptionId;
    private final Collection<MemberId> additionalSubscriberIds;

    /**
     * this constructor is intentionally package protected so that only the MembershipPeriod can create it
     */
    SubscriptionRequest(MembershipPeriodId membershipPeriodId, SubscriptionOptionId subscriptionOptionId,
            Collection<MemberId> additionalSubscriberIds) {
        this.membershipPeriodId = membershipPeriodId;
        this.subscriptionOptionId = subscriptionOptionId;
        this.additionalSubscriberIds = ImmutableList.copyOf(additionalSubscriberIds);
    }

    public MembershipPeriodId getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public SubscriptionOptionId getSubscriptionOptionId() {
        return subscriptionOptionId;
    }

    public Collection<MemberId> getAdditionalSubscriberIds() {
        return additionalSubscriberIds;
    }
}
