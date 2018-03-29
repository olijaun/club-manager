package org.jaun.clubmanager.member.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;

public class PersonNameChangedEvent extends DomainEvent<ContactEventType> {

    private final ContactId contactId;
    private final String firstName;
    private final String middleName;
    private final String lastName;

    public PersonNameChangedEvent(ContactId contactId, String firstName, String middleName, String lastName) {
        super(ContactEventType.NAME_CHANGED);
        this.contactId = requireNonNull(contactId);
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = requireNonNull(lastName);
    }

    public ContactId getContactId() {
        return contactId;
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public Optional<String> getMiddleName() {
        return Optional.ofNullable(middleName);
    }

    public String getLastName() {
        return lastName;
    }
}
