package org.jaun.clubmanager.member.infra.repository;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import org.jaun.clubmanager.domain.model.commons.EventMapping;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipEvent;

public enum MembershipEventMapping implements EventMapping {

    MEMBERSHIP_CREATED("MembershipCreated", MembershipCreatedEvent.class);

    private final String name;
    private final Class<? extends MembershipEvent> eventClass;

    MembershipEventMapping(String name, Class<? extends MembershipEvent> eventClass) {
        this.name = requireNonNull(name);
        this.eventClass = requireNonNull(eventClass);
    }

    @Override
    public Class<? extends MembershipEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public String getEventType() {
        return name;
    }

    public static MembershipEventMapping of(String name) {
        return Stream.of(MembershipEventMapping.values())
                .filter(m -> m.getEventType().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for name " + name));
    }

    public static MembershipEventMapping of(MembershipEvent event) {
        return Stream.of(MembershipEventMapping.values())
                .filter(m -> m.getEventClass().equals(event.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for event " + event));
    }
}
