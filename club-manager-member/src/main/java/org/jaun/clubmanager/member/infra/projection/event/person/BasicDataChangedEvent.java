package org.jaun.clubmanager.member.infra.projection.event.person;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

import org.jaun.clubmanager.member.domain.model.person.PersonId;

public class BasicDataChangedEvent {

    private final PersonId personId;
    private final Name name;

    public BasicDataChangedEvent(PersonId personId, Name name) {
        this.personId = requireNonNull(personId);
        this.name = requireNonNull(name);
    }

    public PersonId getPersonId() {
        return personId;
    }

    public Name getName() {
        return name;
    }
}
