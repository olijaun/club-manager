package org.jaun.clubmanager.member.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventType;

public enum ContactEventType implements EventType {
    CONTACT_CREATED("ContactCreated", ContactCreatedEvent.class), NAME_CHANGED("NameChanged",
            NameChangedEvent.class), ADDRESS_CHANGED("AddressChanged", AddressChangedEvent.class), PHONE_CHANGED("PhoneChanged",
            PhoneChangedEvent.class);

    private final String name;
    private final Class<? extends DomainEvent> eventClass;

    ContactEventType(String name, Class<? extends DomainEvent> eventClass) {
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
