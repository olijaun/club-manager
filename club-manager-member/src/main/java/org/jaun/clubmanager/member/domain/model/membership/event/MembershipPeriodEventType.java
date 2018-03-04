package org.jaun.clubmanager.member.domain.model.membership.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventType;

import static java.util.Objects.requireNonNull;

public enum MembershipPeriodEventType implements EventType {
    MEMBERSHIP_PERIOD_CREATED("MembershipPeriodCreated", MembershipPeriodCreatedEvent.class),
    METADATA_CHANGED("MetadataChanged", MembershipPeriodMetadataChangedEvent.class),
    SUBSCRIPTION_ADDED("SubscriptionAdded", MembershipPeriodSubscriptionAddedEvent.class),
    SUBSCRIPTION_DEFINITION_ADDED("SubscriptionDefinitionAdded", MembershipPeriodSubscriptionDefinitionAddedEvent.class);

    private final String name;
    private final Class<? extends DomainEvent> eventClass;

    MembershipPeriodEventType(String name, Class<? extends DomainEvent> eventClass) {
        this.name = requireNonNull(name);
        this.eventClass = requireNonNull(eventClass);
    }

    @Override
    public String getName() {
        return name;
    }

    public Class<? extends DomainEvent> getEventClass() {
        return eventClass;
    }
}
