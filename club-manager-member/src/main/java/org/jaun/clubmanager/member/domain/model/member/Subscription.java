package org.jaun.clubmanager.member.domain.model.member;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.SubscriptionOptionId;

import com.google.common.collect.ImmutableList;

public class Subscription extends Entity<SubscriptionId> {

    private final SubscriptionId id;
    private final SubscriptionOptionId subscriptionOptionId;
    private final MembershipPeriodId membershipPeriodId; // TODO: unused?
    private final MemberId subscriberId;
    private final Collection<MemberId> additionalSubscriberIds;

    public Subscription(SubscriptionId id, MembershipPeriodId periodId, SubscriptionOptionId subscriptionOptionId,
            MemberId subscriberId, Collection<MemberId> additionalSubscriberIds) {

        this.id = requireNonNull(id);
        this.membershipPeriodId = requireNonNull(periodId);
        this.subscriptionOptionId = requireNonNull(subscriptionOptionId);
        this.subscriberId = requireNonNull(subscriberId);
        this.additionalSubscriberIds = ImmutableList.copyOf(requireNonNull(additionalSubscriberIds));
    }

    public SubscriptionOptionId getSubscriptionOptionId() {
        return subscriptionOptionId;
    }

    public MembershipPeriodId getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public MemberId getSubscriberId() {
        return subscriberId;
    }

    public Collection<MemberId> getAdditionalSubscriberIds() {
        return additionalSubscriberIds;
    }

    public boolean matchesPeriodAndOption(MembershipPeriodId membershipPeriodId, SubscriptionOptionId subscriptionOptionId) {
        return this.membershipPeriodId.equals(membershipPeriodId) && this.subscriptionOptionId.equals(subscriptionOptionId);
    }

    @Override
    public SubscriptionId getId() {
        return id;
    }
}
