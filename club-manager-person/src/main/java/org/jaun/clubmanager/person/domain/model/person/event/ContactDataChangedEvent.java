package org.jaun.clubmanager.person.domain.model.person.event;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.person.domain.model.person.EmailAddress;
import org.jaun.clubmanager.person.domain.model.person.PersonId;
import org.jaun.clubmanager.person.domain.model.person.PhoneNumber;

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
