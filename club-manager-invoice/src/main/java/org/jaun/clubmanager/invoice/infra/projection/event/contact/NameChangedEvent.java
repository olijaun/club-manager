package org.jaun.clubmanager.invoice.infra.projection.event.contact;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.invoice.domain.model.contact.ContactId;

public class NameChangedEvent {

    private final ContactId contactId;
    private final Name name;

    public NameChangedEvent(ContactId contactId, Name name) {
        this.contactId = requireNonNull(contactId);
        this.name = requireNonNull(name);
    }

    public ContactId getContactId() {
        return contactId;
    }

    public Name getName() {
        return name;
    }
}
