package org.jaun.clubmanager.contact.domain.model.contact.event;

import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.contact.domain.model.contact.StreetAddress;
import org.jaun.clubmanager.contact.domain.model.contact.ContactId;

public class StreetAddressChangedEvent extends ContactEvent {

    private final ContactId contactId;
    private final StreetAddress streetAddress;

    public StreetAddressChangedEvent(ContactId contactId, StreetAddress streetAddress) {
        this.contactId = contactId;
        this.streetAddress = streetAddress;
    }

    public ContactId getContactId() {
        return contactId;
    }

    public Optional<StreetAddress> getStreetAddress() {
        return Optional.ofNullable(streetAddress);
    }
}
