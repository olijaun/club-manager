package org.jaun.clubmanager.member.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.Sex;

public class SexChangedEvent extends DomainEvent<ContactEventType> {

    private final ContactId contactId;
    private final Sex sex;

    public SexChangedEvent(ContactId contactId, Sex sex) {
        super(ContactEventType.NAME_CHANGED);
        this.contactId = requireNonNull(contactId);
        this.sex = sex;
    }

    public ContactId getContactId() {
        return contactId;
    }

    public Sex getSex() {
        return sex;
    }
}
