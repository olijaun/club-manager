package org.jaun.clubmanager.member.domain.model.membership;

import java.util.Collection;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipCreatedEvent;

import com.google.common.collect.ImmutableList;

public class Membership extends EventSourcingAggregate<MembershipId> {

    private MembershipId id;
    private SubscriptionOptionId subscriptionOptionId;
    private MembershipPeriodId membershipPeriodId;
    private MemberId subscriberId;
    private Collection<MemberId> additionalSubscriberIds;

    public Membership(MembershipId id, MembershipPeriod period, SubscriptionOptionId subscriptionOptionId,
            MemberId subscriberId, Collection<MemberId> additionalSubscriberIds) {

        SubscriptionOption option = period.getSubscriptionOptionById(subscriptionOptionId)
                .orElseThrow(() -> new IllegalStateException(
                        "option " + subscriptionOptionId + " does not exist in period " + period.getId()));

        if ((additionalSubscriberIds.size() + 1) > option.getMaxSubscribers()) {
            throw new IllegalStateException(
                    "a maximum of " + option.getMaxSubscribers() + " is possible for this subscription type");
        }

        apply(new MembershipCreatedEvent(id, period.getId(), option.getSubscriptionOptionId(), subscriberId,
                additionalSubscriberIds));

    }

    public Membership(EventStream<Membership> eventStream) {
        replayEvents(eventStream);
    }

    public SubscriptionOptionId getSubscriptionOptionId() {
        return subscriptionOptionId;
    }

    public MemberId getSubscriberId() {
        return subscriberId;
    }

    public Collection<MemberId> getAdditionalSubscriberIds() {
        return additionalSubscriberIds;
    }

    protected void mutate(MembershipCreatedEvent event) {
        this.id = event.getMembershipId();
        this.membershipPeriodId = event.getMembershipPeriodId();
        this.subscriptionOptionId = event.getSubscriptionOptionId();
        this.subscriberId = event.getMemberId();
        this.additionalSubscriberIds = ImmutableList.copyOf(event.getAdditionalSubscriberIds());
    }

    @Override
    protected void mutate(DomainEvent event) {
        if (event instanceof MembershipCreatedEvent) {
            mutate((MembershipCreatedEvent) event);
        }
    }

    public MembershipId getId() {
        return id;
    }

}
