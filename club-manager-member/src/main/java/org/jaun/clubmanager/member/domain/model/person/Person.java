package org.jaun.clubmanager.member.domain.model.person;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

public class Person implements Serializable {
    private final PersonId personId;

    public Person(PersonId personId) {
        this.personId = requireNonNull(personId);
    }

    public PersonId getPersonId() {
        return personId;
    }
}
