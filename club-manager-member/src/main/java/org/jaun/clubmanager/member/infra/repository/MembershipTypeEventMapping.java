package org.jaun.clubmanager.member.infra.repository;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import org.jaun.clubmanager.domain.model.commons.EventMapping;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeEvent;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeMetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodEvent;

public enum MembershipTypeEventMapping implements EventMapping {
    MEMBERSHIPTYPE_CREATED("MembershipTypeCreated", MembershipTypeCreatedEvent.class), //
    MEMBERSHIPTYPE_METADATA_CHANGED("MembershipTypeMetadataChanged", MembershipTypeMetadataChangedEvent.class);

    private final String name;
    private final Class<? extends MembershipTypeEvent> eventClass;

    MembershipTypeEventMapping(String name, Class<? extends MembershipTypeEvent> eventClass) {
        this.name = requireNonNull(name);
        this.eventClass = requireNonNull(eventClass);
    }

    @Override
    public Class<? extends MembershipTypeEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public EventType getEventType() {
        return new EventType(name);
    }

    public static MembershipTypeEventMapping of(EventType name) {
        return Stream.of(MembershipTypeEventMapping.values())
                .filter(m -> m.getEventType().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for name " + name));
    }

    public static MembershipTypeEventMapping of(MembershipTypeEvent event) {
        return Stream.of(MembershipTypeEventMapping.values())
                .filter(m -> m.getEventClass().equals(event.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for event " + event));
    }
}
