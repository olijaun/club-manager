package org.jaun.clubmanager.contact.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.contact.domain.model.contact.ContactId;
import org.jaun.clubmanager.contact.domain.model.contact.Sex;

public class SexChangedEvent extends ContactEvent {

    private final ContactId contactId;
    private final Sex sex;

    public SexChangedEvent(ContactId contactId, Sex sex) {
        this.contactId = requireNonNull(contactId);
        this.sex = sex;
    }

    public ContactId getContactId() {
        return contactId;
    }

    public Optional<Sex> getSex() {
        return Optional.ofNullable(sex);
    }
}
