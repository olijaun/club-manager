package org.jaun.clubmanager.member.domain.model.membership;

import java.util.Collection;

import org.jaun.clubmanager.domain.model.commons.Entity;

import com.google.common.collect.ImmutableList;

public class Subscription extends Entity<SubscriptionId> {

    private final SubscriptionId id;
    private final MemberId memberId;
    private final SubscriptionOptionId subscriptionOptionId;
    private final Collection<MemberId> additionalSubscribers;

    public Subscription(SubscriptionId id, MemberId memberId, Collection<MemberId> additionalSubscribers,
            SubscriptionOptionId subscriptionOptionId) {
        this.id = id;
        this.memberId = memberId;
        this.subscriptionOptionId = subscriptionOptionId;
        this.additionalSubscribers = ImmutableList.copyOf(additionalSubscribers);
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public Collection<MemberId> getAdditionalSubscribers() {
        return additionalSubscribers;
    }

    public SubscriptionOptionId getSubscriptionOptionId() {
        return subscriptionOptionId;
    }

    @Override
    public SubscriptionId getId() {
        return id;
    }
}
