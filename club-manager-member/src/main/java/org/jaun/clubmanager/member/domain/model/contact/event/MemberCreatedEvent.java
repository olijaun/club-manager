package org.jaun.clubmanager.member.domain.model.contact.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;

import static java.util.Objects.requireNonNull;

public class MemberCreatedEvent extends DomainEvent<ContactEventType> {

    private final ContactId contactId;

    public MemberCreatedEvent(ContactId contactId) {
        super(ContactEventType.MEMBER_CREATED);
        this.contactId = requireNonNull(contactId);
    }

    public ContactId getContactId() {
        return contactId;
    }
}
