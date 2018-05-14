package org.jaun.clubmanager.contact.domain.model.contact;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

public class Name extends ValueObject {

    private final String lastNameOrCompanyName;
    private final String firstName;

    public Name(String lastNameOrCompanyName, String firstName) {
        this.lastNameOrCompanyName = requireNonNull(lastNameOrCompanyName);
        this.firstName = firstName;
    }

    public String getLastNameOrCompanyName() {
        return lastNameOrCompanyName;
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }
}
