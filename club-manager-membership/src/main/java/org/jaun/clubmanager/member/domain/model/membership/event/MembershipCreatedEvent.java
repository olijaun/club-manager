package org.jaun.clubmanager.member.domain.model.membership.event;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import org.jaun.clubmanager.member.domain.model.membership.MemberId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.SubscriptionOptionId;

import com.google.common.collect.ImmutableList;

public class MembershipCreatedEvent extends MembershipEvent {

    private final MembershipId membershipId;
    private final MembershipPeriodId membershipPeriodId;
    private final SubscriptionOptionId subscriptionOptionId;
    private final MemberId memberId;
    private final Collection<MemberId> additionalSubscriberIds;

    public MembershipCreatedEvent(MembershipId membershipId, MembershipPeriodId membershipPeriodId,
            SubscriptionOptionId subscriptionOptionId, MemberId memberId,
            Collection<MemberId> additionalSubscriberIds) {

        this.membershipId = requireNonNull(membershipId);
        this.membershipPeriodId = requireNonNull(membershipPeriodId);
        this.subscriptionOptionId = requireNonNull(subscriptionOptionId);
        this.memberId = requireNonNull(memberId);
        this.additionalSubscriberIds = ImmutableList.copyOf(additionalSubscriberIds);
    }

    public MembershipId getMembershipId() {
        return membershipId;
    }

    public SubscriptionOptionId getSubscriptionOptionId() {
        return subscriptionOptionId;
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public Collection<MemberId> getAdditionalSubscriberIds() {
        return additionalSubscriberIds;
    }

    public MembershipPeriodId getMembershipPeriodId() {
        return membershipPeriodId;
    }
}
