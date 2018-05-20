package org.jaun.clubmanager.invoice.domain.model.contact;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Optional;

public class Contact implements Serializable {
    private final ContactId contactId;

    public Contact(ContactId contactId) {
        this.contactId = requireNonNull(contactId);
    }

    public ContactId getContactId() {
        return contactId;
    }
}
