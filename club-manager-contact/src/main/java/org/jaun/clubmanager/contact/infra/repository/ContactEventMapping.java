package org.jaun.clubmanager.contact.infra.repository;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import org.jaun.clubmanager.domain.model.commons.EventMapping;
import org.jaun.clubmanager.contact.domain.model.contact.event.BirthDateChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.ContactCreatedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.ContactEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.EmailAddressChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.NameChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.PhoneNumberChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.SexChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.StreetAddressChangedEvent;

public enum ContactEventMapping implements EventMapping {
    CONTACT_CREATED("ContactCreated", ContactCreatedEvent.class), //
    NAME_CHANGED("NameChanged", NameChangedEvent.class), //
    ADDRESS_CHANGED("StreetAddressChanged", StreetAddressChangedEvent.class), //
    PHONE_CHANGED("PhoneNumberChanged", PhoneNumberChangedEvent.class), //
    EMAIL_CHANGED("EmailAddressChanged", EmailAddressChangedEvent.class), //
    SEX_CHANGED("SexChanged", SexChangedEvent.class), //
    BIRTHDATE_CHANGED("BirthDateChanged", BirthDateChangedEvent.class);

    private final String name;
    private final Class<? extends ContactEvent> eventClass;

    ContactEventMapping(String name, Class<? extends ContactEvent> eventClass) {
        this.name = requireNonNull(name);
        this.eventClass = requireNonNull(eventClass);
    }

    @Override
    public Class<? extends ContactEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public String getEventType() {
        return name;
    }

    public static ContactEventMapping of(String name) {
        return Stream.of(ContactEventMapping.values())
                .filter(m -> m.getEventType().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for name " + name));
    }

    public static ContactEventMapping of(ContactEvent event) {
        return Stream.of(ContactEventMapping.values())
                .filter(m -> m.getEventClass().equals(event.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for event " + event));
    }
}
