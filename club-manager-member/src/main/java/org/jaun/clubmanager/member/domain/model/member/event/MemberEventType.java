package org.jaun.clubmanager.member.domain.model.member.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventType;
import org.jaun.clubmanager.member.domain.model.member.PhoneNumber;

import static java.util.Objects.requireNonNull;

public enum MemberEventType implements EventType {
    MEMBER_CREATED("MemberCreated", MemberCreatedEvent.class),
    NAME_CHANGED("NameChanged", NameChangedEvent.class),
    ADDRESS_CHANGED("AddressChanged", AddressChangedEvent.class),
    PHONE_CHANGED("PhoneChanged", PhoneChangedEvent.class);

    private final String name;
    private final Class<? extends DomainEvent> eventClass;

    MemberEventType(String name, Class<? extends DomainEvent> eventClass) {
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
