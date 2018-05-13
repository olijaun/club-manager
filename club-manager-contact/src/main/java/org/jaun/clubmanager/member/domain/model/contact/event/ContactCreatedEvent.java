package org.jaun.clubmanager.member.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.ContactType;
import org.jaun.clubmanager.member.domain.model.contact.Name;

public class ContactCreatedEvent extends ContactEvent {

    private final ContactId contactId;
    private final ContactType contactType;

    public ContactCreatedEvent(ContactId contactId, ContactType contactType) {
        this.contactId = requireNonNull(contactId);
        this.contactType = requireNonNull(contactType);
    }

    public ContactId getContactId() {
        return contactId;
    }

    public ContactType getContactType() {
        return contactType;
    }
}
