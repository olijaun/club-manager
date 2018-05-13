package org.jaun.clubmanager.member.domain.model.member.event;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.SubscriptionOptionId;

import com.google.common.collect.ImmutableList;

public class SubscriptionCreatedEvent extends MemberEvent {

    private final SubscriptionId subscriptionId;
    private final MemberId memberId;
    private final MembershipPeriodId membershipPeriodId;
    private final SubscriptionOptionId subscriptionOptionId;
    private final Collection<MemberId> additionalSubscriberIds;

    public SubscriptionCreatedEvent(SubscriptionId subscriptionId, MemberId memberId, MembershipPeriodId membershipPeriodId,
            SubscriptionOptionId subscriptionOptionId, Collection<MemberId> additionalSubscriberIds) {

        this.subscriptionId = subscriptionId;
        this.memberId = requireNonNull(memberId);
        this.membershipPeriodId = membershipPeriodId;
        this.subscriptionOptionId = subscriptionOptionId;
        this.additionalSubscriberIds = ImmutableList.copyOf(additionalSubscriberIds);
    }

    public SubscriptionId getSubscriptionId() {
        return subscriptionId;
    }

    public MemberId getMemberId() {
        return memberId;
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
