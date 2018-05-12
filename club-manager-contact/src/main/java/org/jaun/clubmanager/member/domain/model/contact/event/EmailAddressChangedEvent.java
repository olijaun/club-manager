package org.jaun.clubmanager.member.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.EmailAddress;

public class EmailAddressChangedEvent extends DomainEvent<ContactEventType> {

    private final ContactId contactId;
    private final EmailAddress emailAddress;

    public EmailAddressChangedEvent(ContactId contactId, EmailAddress emailAddress) {
        super(ContactEventType.PHONE_CHANGED);
        this.contactId = requireNonNull(contactId);
        this.emailAddress = emailAddress;
    }

    public ContactId getContactId() {
        return contactId;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }
}
