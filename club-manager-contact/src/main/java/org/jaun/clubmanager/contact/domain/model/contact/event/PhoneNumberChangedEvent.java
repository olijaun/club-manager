package org.jaun.clubmanager.contact.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.contact.domain.model.contact.ContactId;
import org.jaun.clubmanager.contact.domain.model.contact.PhoneNumber;

public class PhoneNumberChangedEvent extends ContactEvent {

    private final ContactId contactId;
    private final PhoneNumber phoneNumber;

    public PhoneNumberChangedEvent(ContactId contactId, PhoneNumber phoneNumber) {
        this.contactId = requireNonNull(contactId);
        this.phoneNumber = phoneNumber;
    }

    public ContactId getContactId() {
        return contactId;
    }

    public Optional<PhoneNumber> getPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }
}
