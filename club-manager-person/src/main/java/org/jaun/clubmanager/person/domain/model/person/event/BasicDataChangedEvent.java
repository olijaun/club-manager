package org.jaun.clubmanager.person.domain.model.person.event;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Optional;

import org.jaun.clubmanager.person.domain.model.person.Name;
import org.jaun.clubmanager.person.domain.model.person.PersonId;
import org.jaun.clubmanager.person.domain.model.person.Sex;

public class BasicDataChangedEvent extends PersonEvent {

    private final PersonId personId;
    private final Name name;
    private final LocalDate birthDate;
    private final Sex sex;

    public BasicDataChangedEvent(PersonId personId, Name name, LocalDate birthDate, Sex sex) {
        this.personId = requireNonNull(personId);
        this.name = requireNonNull(name);
        this.birthDate = birthDate;
        this.sex = sex;
    }

    public PersonId getPersonId() {
        return personId;
    }

    public Name getName() {
        return name;
    }

    public Optional<LocalDate> getBirthDate() {
        return Optional.ofNullable(birthDate);
    }

    public Optional<Sex> getSex() {
        return Optional.ofNullable(sex);
    }
}
