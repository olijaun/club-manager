package org.jaun.clubmanager.member.domain.model.contact;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PersonName extends Name {
    private final String firstName;
    private final String lastName;
    private final String middleName;

    public PersonName(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = requireNonNull(lastName);
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public Optional<String> getMiddleName() {
        return Optional.ofNullable(middleName);
    }

    @Override
    public String getNameLine() {
        return Stream.of(firstName, middleName, lastName).filter(Objects::nonNull).collect(Collectors.joining(" "));
    }
}
