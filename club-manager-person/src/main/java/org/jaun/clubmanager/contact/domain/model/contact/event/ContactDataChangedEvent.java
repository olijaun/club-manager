package org.jaun.clubmanager.contact.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.contact.domain.model.contact.EmailAddress;
import org.jaun.clubmanager.contact.domain.model.contact.PersonId;
import org.jaun.clubmanager.contact.domain.model.contact.PhoneNumber;

public class ContactDataChangedEvent extends PersonEvent {

    private final PersonId personId;
    private final EmailAddress emailAddress;
    private final PhoneNumber phoneNumber;

    public ContactDataChangedEvent(PersonId personId, EmailAddress emailAddress, PhoneNumber phoneNumber) {
        this.personId = requireNonNull(personId);
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
    }

    public PersonId getPersonId() {
        return personId;
    }

    public Optional<EmailAddress> getEmailAddress() {
        return Optional.ofNullable(emailAddress);
    }

    public Optional<PhoneNumber> getPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }
}
