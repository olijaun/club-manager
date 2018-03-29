package org.jaun.clubmanager.member.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;

public class CompanyNameChangedEvent extends DomainEvent<ContactEventType> {

    private final ContactId contactId;
    private final String name;

    public CompanyNameChangedEvent(ContactId contactId, String name) {
        super(ContactEventType.NAME_CHANGED);
        this.contactId = requireNonNull(contactId);
        this.name = requireNonNull(name);
    }

    public ContactId getContactId() {
        return contactId;
    }

    public String getName() {
        return name;
    }
}
