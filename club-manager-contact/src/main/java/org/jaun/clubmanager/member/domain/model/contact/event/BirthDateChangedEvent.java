package org.jaun.clubmanager.member.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;

public class BirthDateChangedEvent extends DomainEvent<ContactEventType> {

    private final ContactId contactId;
    private final LocalDate birthDate;

    public BirthDateChangedEvent(ContactId contactId, LocalDate birthDate) {
        super(ContactEventType.BIRTHDATE_CHANGED);
        this.contactId = requireNonNull(contactId);
        this.birthDate = birthDate;
    }

    public ContactId getContactId() {
        return contactId;
    }

    public Optional<LocalDate> getBirthDate() {
        return Optional.ofNullable(birthDate);
    }
}
