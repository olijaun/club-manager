package org.jaun.clubmanager.member.domain.model.contact;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactCreatedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.NameChangedEvent;

public abstract class Contact extends EventSourcingAggregate<ContactId> {

    private ContactId id;
    private ContactType contactType;
    private Name name;
    private Address address;
    private PhoneNumber phone;
    private EmailAddress emailAddress;

    public Contact(ContactId id, ContactType contactType) {
        apply(new ContactCreatedEvent(id, contactType));
    }

    public Contact(EventStream<? extends Contact> eventStream) {
        replayEvents(eventStream);
    }

    protected void mutate(ContactCreatedEvent event) {
        this.id = event.getContactId();
        this.contactType = event.getContactType();
    }

    @Override
    protected void mutate(DomainEvent event) {
        if (event instanceof ContactCreatedEvent) {
            mutate((ContactCreatedEvent) event);
        }
    }

    public ContactId getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public Optional<EmailAddress> getEmailAddress() {
        return Optional.ofNullable(emailAddress);
    }

    public static abstract class Builder {
        private ContactId id;
        private ContactType contactType;
        private Name name;
        private Address address;
        private PhoneNumber phone;
        private EmailAddress emailAddress;

        public abstract void build();

        public Builder id(ContactId id) {
            this.id = id;
            return this;
        }

        public Builder contactType(ContactType contactType) {
            this.contactType = contactType;
            return this;
        }

        public Builder name(Name name) {
            this.name = name;
            return this;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public Builder phone(PhoneNumber phone) {
            this.phone = phone;
            return this;
        }

        public Builder emailAddress(EmailAddress emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }
    }
}
