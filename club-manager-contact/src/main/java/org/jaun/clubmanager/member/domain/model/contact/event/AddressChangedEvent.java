package org.jaun.clubmanager.member.domain.model.contact.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.Address;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;

public class AddressChangedEvent extends DomainEvent<ContactEventType> {

    private final ContactId contactId;
    private final Address address;

    public AddressChangedEvent(ContactId contactId, Address address) {
        super(ContactEventType.ADDRESS_CHANGED);
        this.contactId = contactId;
        this.address = address;
    }

    public ContactId getContactId() {
        return contactId;
    }

    public Address getAddress() {
        return address;
    }
}
