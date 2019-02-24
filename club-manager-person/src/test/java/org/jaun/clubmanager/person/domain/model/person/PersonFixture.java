package org.jaun.clubmanager.person.domain.model.person;

import java.time.LocalDate;

class PersonFixture {

    public static Person.Builder newPerson() {

        PersonId personId = new PersonId("P12345678");
        Name name = new Name("de Lucia", "Paco");
        LocalDate birthDate = LocalDate.of(1947, 12, 21);

        return Person.builder()
                .id(personId)
                .personType(PersonType.NATURAL)
                .name(name)
                .birthDate(birthDate)
                .gender(Gender.MALE);
    }
}