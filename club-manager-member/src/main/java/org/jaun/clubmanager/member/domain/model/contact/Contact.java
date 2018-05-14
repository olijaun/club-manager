package org.jaun.clubmanager.member.domain.model.contact;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Optional;

public class Contact implements Serializable {
    private final ContactId contactId;
    private final String firstName;
    private final String lastNameOrCompanyName;

    public Contact(ContactId contactId, String firstName, String lastNameOrCompanyName) {
        this.contactId = requireNonNull(contactId);
        this.firstName = firstName;
        this.lastNameOrCompanyName = requireNonNull(lastNameOrCompanyName);
    }

    public ContactId getContactId() {
        return contactId;
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public String getLastNameOrCompanyName() {
        return lastNameOrCompanyName;
    }
}
