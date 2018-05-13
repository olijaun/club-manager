package org.jaun.clubmanager.member.infra.projection.event.contact;

import static java.util.Objects.requireNonNull;

public class ContactCreatedEvent {

    private final ContactId contactId;

    public ContactCreatedEvent(ContactId contactId) {
        this.contactId = requireNonNull(contactId);
    }

    public ContactId getContactId() {
        return contactId;
    }
}
