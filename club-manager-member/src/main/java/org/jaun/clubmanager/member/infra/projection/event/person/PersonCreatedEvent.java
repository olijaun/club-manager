package org.jaun.clubmanager.member.infra.projection.event.person;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.member.domain.model.person.PersonId;

public class PersonCreatedEvent {

    private final PersonId personId;

    public PersonCreatedEvent(PersonId personId) {
        this.personId = requireNonNull(personId);
    }

    public PersonId getPersonId() {
        return personId;
    }
}
