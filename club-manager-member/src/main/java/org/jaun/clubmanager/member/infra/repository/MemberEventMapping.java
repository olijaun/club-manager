package org.jaun.clubmanager.member.infra.repository;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import org.jaun.clubmanager.domain.model.commons.EventMapping;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.member.domain.model.member.event.MemberCreatedEvent;
import org.jaun.clubmanager.member.domain.model.member.event.MemberEvent;
import org.jaun.clubmanager.member.domain.model.member.event.SubscriptionCreatedEvent;

public enum MemberEventMapping implements EventMapping {

    MEMBER_CREATED("MemberCreated", MemberCreatedEvent.class), //
    SUBSCRIPTION_CREATED("SubscriptionCreated", SubscriptionCreatedEvent.class);

    private final EventType eventType;
    private final Class<? extends MemberEvent> eventClass;

    MemberEventMapping(String name, Class<? extends MemberEvent> eventClass) {
        this.eventType = new EventType(name);
        this.eventClass = requireNonNull(eventClass);
    }

    @Override
    public Class<? extends MemberEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    public static MemberEventMapping of(EventType name) {
        return Stream.of(MemberEventMapping.values())
                .filter(m -> m.getEventType().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for eventType " + name));
    }

    public static MemberEventMapping of(MemberEvent event) {
        return Stream.of(MemberEventMapping.values())
                .filter(m -> m.getEventClass().equals(event.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for event " + event));
    }
}
