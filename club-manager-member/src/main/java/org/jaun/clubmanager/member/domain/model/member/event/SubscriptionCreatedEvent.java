package org.jaun.clubmanager.member.domain.model.member.event;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;

import com.google.common.collect.ImmutableList;

public class SubscriptionCreatedEvent extends MemberEvent {

    private final SubscriptionId subscriptionId;
    private final MemberId memberId;
    private final SubscriptionPeriodId subscriptionPeriodId;
    private final SubscriptionTypeId subscriptionTypeId;
    private final Collection<MemberId> additionalSubscriberIds;

    public SubscriptionCreatedEvent(SubscriptionId subscriptionId, MemberId memberId, SubscriptionPeriodId subscriptionPeriodId,
            SubscriptionTypeId subscriptionTypeId, Collection<MemberId> additionalSubscriberIds) {

        this.subscriptionId = subscriptionId;
        this.memberId = requireNonNull(memberId);
        this.subscriptionPeriodId = subscriptionPeriodId;
        this.subscriptionTypeId = subscriptionTypeId;
        this.additionalSubscriberIds = ImmutableList.copyOf(additionalSubscriberIds);
    }

    public SubscriptionId getSubscriptionId() {
        return subscriptionId;
    }

    public MemberId getMemberId() {
        return memberId;
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
