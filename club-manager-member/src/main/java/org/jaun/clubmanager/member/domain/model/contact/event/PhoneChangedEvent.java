package org.jaun.clubmanager.member.domain.model.contact.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.PhoneNumber;

import static java.util.Objects.requireNonNull;

public class PhoneChangedEvent extends DomainEvent<ContactEventType> {

    private final ContactId contactId;
    private final PhoneNumber phoneNumber;

    public PhoneChangedEvent(ContactId contactId, PhoneNumber phoneNumber) {
        super(ContactEventType.PHONE_CHANGED);
        this.contactId = requireNonNull(contactId);
        this.phoneNumber = phoneNumber;
    }

    public ContactId getContactId() {
        return contactId;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }
}
