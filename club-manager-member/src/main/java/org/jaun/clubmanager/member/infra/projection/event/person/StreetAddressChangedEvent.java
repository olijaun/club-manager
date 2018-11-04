package org.jaun.clubmanager.member.infra.projection.event.person;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.member.domain.model.person.PersonId;

public class StreetAddressChangedEvent {

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
