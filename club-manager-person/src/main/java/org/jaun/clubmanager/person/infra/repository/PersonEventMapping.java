package org.jaun.clubmanager.person.infra.repository;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import org.jaun.clubmanager.person.domain.model.person.event.BasicDataChangedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.ContactDataChangedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonCreatedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonEvent;
import org.jaun.clubmanager.person.domain.model.person.event.StreetAddressChangedEvent;
import org.jaun.clubmanager.domain.model.commons.EventMapping;
import org.jaun.clubmanager.eventstore.EventType;

public enum PersonEventMapping implements EventMapping {
    PERSON_CREATED("PersonCreated", PersonCreatedEvent.class), //
    BASIC_DATA_CHANGED("BasicDataChanged", BasicDataChangedEvent.class), //
    CONTACT_DATA_CHANGED("ContactDataChanged", ContactDataChangedEvent.class), //
    STREE_ADDRESS_CHANGED("StreetAddressChanged", StreetAddressChangedEvent.class);

    private final EventType eventType;
    private final Class<? extends PersonEvent> eventClass;

    PersonEventMapping(String name, Class<? extends PersonEvent> eventClass) {
        this.eventType = new EventType(name);
        this.eventClass = requireNonNull(eventClass);
    }

    @Override
    public Class<? extends PersonEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    public static PersonEventMapping of(EventType name) {
        return Stream.of(PersonEventMapping.values())
                .filter(m -> m.getEventType().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for name " + name));
    }

    public static PersonEventMapping of(PersonEvent event) {
        return Stream.of(PersonEventMapping.values())
                .filter(m -> m.getEventClass().equals(event.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for event " + event));
    }
}
