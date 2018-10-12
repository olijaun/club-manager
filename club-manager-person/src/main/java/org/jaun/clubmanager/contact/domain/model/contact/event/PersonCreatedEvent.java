package org.jaun.clubmanager.contact.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.contact.domain.model.contact.PersonId;
import org.jaun.clubmanager.contact.domain.model.contact.PersonType;

public class PersonCreatedEvent extends PersonEvent {

    private final PersonId personId;
    private final PersonType personType;

    public PersonCreatedEvent(PersonId personId, PersonType personType) {
        this.personId = requireNonNull(personId);
        this.personType = requireNonNull(personType);
    }

    public PersonId getPersonId() {
        return personId;
    }

    public PersonType getPersonType() {
        return personType;
    }
}
