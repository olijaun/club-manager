package org.jaun.clubmanager.member.domain.model.contact.event;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.EmailAddress;

public class EmailAddressChangedEvent extends ContactEvent {

    private final ContactId contactId;
    private final EmailAddress emailAddress;

    public EmailAddressChangedEvent(ContactId contactId, EmailAddress emailAddress) {
        this.contactId = requireNonNull(contactId);
        this.emailAddress = emailAddress;
    }

    public ContactId getContactId() {
        return contactId;
    }

    public Optional<EmailAddress> getEmailAddress() {
        return Optional.ofNullable(emailAddress);
    }
}
