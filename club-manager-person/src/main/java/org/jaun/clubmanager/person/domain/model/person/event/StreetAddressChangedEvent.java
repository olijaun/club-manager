package org.jaun.clubmanager.person.domain.model.person.event;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;

import org.jaun.clubmanager.person.domain.model.person.StreetAddress;
import org.jaun.clubmanager.person.domain.model.person.PersonId;

public class StreetAddressChangedEvent extends PersonEvent {

    private final PersonId personId;
    private final StreetAddress streetAddress;

    public StreetAddressChangedEvent(PersonId personId, StreetAddress streetAddress) {
        this.personId = requireNonNull(personId);
        this.streetAddress = requireNonNull(streetAddress);
    }

    public PersonId getPersonId() {
        return personId;
    }

    public StreetAddress getStreetAddress() {
        return streetAddress;
    }
}
