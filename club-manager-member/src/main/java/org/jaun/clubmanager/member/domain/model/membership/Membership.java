package org.jaun.clubmanager.member.domain.model.membership;

import java.util.Collection;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipCreatedEvent;

import com.google.common.collect.ImmutableList;

public class Membership extends EventSourcingAggregate<MembershipId> {

    private MembershipId id;
    private SubscriptionDefinitionId subscriptionDefinitionId;
    private MembershipPeriodId membershipPeriodId;
    private ContactId subscriberId;
    private Collection<ContactId> additionalSubscriberIds;

    public Membership(MembershipId id, MembershipPeriod period, SubscriptionDefinitionId subscriptionDefinitionId,
            ContactId subscriberId, Collection<ContactId> additionalSubscriberIds) {

        SubscriptionDefinition definition = period.getSubscriptionDefinitionById(subscriptionDefinitionId)
                .orElseThrow(() -> new IllegalStateException(
                        "definition " + subscriptionDefinitionId + " does not exist in period " + period.getId()));

        if ((additionalSubscriberIds.size() + 1) > definition.getMaxSubscribers()) {
            throw new IllegalStateException(
                    "a maximum of " + definition.getMaxSubscribers() + " is possible for this subscription type");
        }

        apply(new MembershipCreatedEvent(id, period.getId(), definition.getSubscriptionDefinitionId(), subscriberId,
                additionalSubscriberIds));

    }

    public Membership(EventStream<Membership> eventStream) {
        replayEvents(eventStream);
    }

    public SubscriptionDefinitionId getSubscriptionDefinitionId() {
        return subscriptionDefinitionId;
    }

    public ContactId getSubscriberId() {
        return subscriberId;
    }

    public Collection<ContactId> getAdditionalSubscriberIds() {
        return additionalSubscriberIds;
    }

    protected void mutate(MembershipCreatedEvent event) {
        this.id = event.getMembershipId();
        this.membershipPeriodId = event.getMembershipPeriodId();
        this.subscriptionDefinitionId = event.getSubscriptionDefinitionId();
        this.subscriberId = event.getSubscriberId();
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
