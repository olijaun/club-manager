package org.jaun.clubmanager.contact.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.contact.domain.model.contact.ContactId;
import org.jaun.clubmanager.contact.domain.model.contact.Name;

public class NameChangedEvent extends ContactEvent {

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
