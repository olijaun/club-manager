package org.jaun.clubmanager.member.infra.projection.event.contact;

import static java.util.Objects.requireNonNull;

public class NameChangedEvent {

    private final ContactId contactId;
    private final String firstName;
    private final String lastName;

    public NameChangedEvent(ContactId contactId, String firstName, String lastName) {
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
