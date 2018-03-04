package org.jaun.clubmanager.member.domain.model.contact.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;

import static java.util.Objects.requireNonNull;

public class ContactCreatedEvent extends DomainEvent<ContactEventType> {

    private final ContactId contactId;

    public ContactCreatedEvent(ContactId contactId) {
        super(ContactEventType.CONTACT_CREATED);
        this.contactId = requireNonNull(contactId);
    }

    public ContactId getContactId() {
        return contactId;
    }
}
