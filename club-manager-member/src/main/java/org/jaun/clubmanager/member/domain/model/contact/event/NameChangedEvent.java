package org.jaun.clubmanager.member.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;

public class NameChangedEvent extends DomainEvent<ContactEventType> {

    private final ContactId contactId;
    private final String firstName;
    private final String lastName;

    public NameChangedEvent(ContactId contactId, String firstName, String lastName) {
        super(ContactEventType.NAME_CHANGED);
        this.contactId = requireNonNull(contactId);
        this.firstName = firstName;
        this.lastName = requireNonNull(lastName);
    }

    public ContactId getContactId() {
        return contactId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
