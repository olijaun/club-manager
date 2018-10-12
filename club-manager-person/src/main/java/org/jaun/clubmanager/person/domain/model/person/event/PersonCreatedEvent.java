package org.jaun.clubmanager.person.domain.model.person.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.person.domain.model.person.PersonId;
import org.jaun.clubmanager.person.domain.model.person.PersonType;

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
