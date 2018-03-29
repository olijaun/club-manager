package org.jaun.clubmanager.member.domain.model.membership.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventType;

public enum MembershipEventType implements EventType {

    MEMBERSHIP_CREATED("MembershipCreated", MembershipCreatedEvent.class);

    private final String name;
    private final Class<? extends DomainEvent> eventClass;

    MembershipEventType(String name, Class<? extends DomainEvent> eventClass) {
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
