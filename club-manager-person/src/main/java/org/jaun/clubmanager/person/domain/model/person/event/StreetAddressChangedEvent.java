package org.jaun.clubmanager.person.domain.model.person.event;

import java.util.Optional;

import org.jaun.clubmanager.person.domain.model.person.StreetAddress;
import org.jaun.clubmanager.person.domain.model.person.PersonId;

public class StreetAddressChangedEvent extends PersonEvent {

    private final PersonId personId;
    private final StreetAddress streetAddress;

    public StreetAddressChangedEvent(PersonId personId, StreetAddress streetAddress) {
        this.personId = personId;
        this.streetAddress = streetAddress;
    }

    public PersonId getPersonId() {
        return personId;
    }

    public Optional<StreetAddress> getStreetAddress() {
        return Optional.ofNullable(streetAddress);
    }
}
