package org.jaun.clubmanager.person.infra.repository;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import org.jaun.clubmanager.domain.model.commons.EventMapping;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.person.domain.model.person.event.BasicDataChangedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.ContactDataChangedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonCreatedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRegistryCreatedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRegistryEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRequestedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.StartFromResetEvent;
import org.jaun.clubmanager.person.domain.model.person.event.StreetAddressChangedEvent;

public enum PersonIdRegistryEventMapping implements EventMapping {
    PERSON_ID_REGISTRY_CREATED("PersonIdRegistryCreated", PersonIdRegistryCreatedEvent.class), //
    PERSON_ID_REQUESTED("PersonIdRequested", PersonIdRequestedEvent.class),
    START_FROM_RESET("StartFromReset", StartFromResetEvent.class);

    private final EventType eventType;
    private final Class<? extends PersonIdRegistryEvent> eventClass;

    PersonIdRegistryEventMapping(String name, Class<? extends PersonIdRegistryEvent> eventClass) {
        this.eventType = new EventType(name);
        this.eventClass = requireNonNull(eventClass);
    }

    @Override
    public Class<? extends PersonIdRegistryEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    public static PersonIdRegistryEventMapping of(EventType name) {
        return Stream.of(PersonIdRegistryEventMapping.values())
                .filter(m -> m.getEventType().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for name " + name));
    }

    public static PersonIdRegistryEventMapping of(PersonIdRegistryEvent event) {
        return Stream.of(PersonIdRegistryEventMapping.values())
                .filter(m -> m.getEventClass().equals(event.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for event " + event));
    }
}
