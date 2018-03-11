package org.jaun.clubmanager.member.domain.model.contact;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactCreatedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.NameChangedEvent;

public class Contact extends EventSourcingAggregate<ContactId> {

    private ContactId id;
    private String firstName;
    private String lastName;
    private Address address;
    private PhoneNumber phone;
    private EmailAddress emailAddress;

    public Contact(ContactId id, String firstName, String lastName) {
        apply(new ContactCreatedEvent(id));
        setName(firstName, lastName);
    }

    public Contact(EventStream<Contact> eventStream) {
        replayEvents(eventStream);
    }

    protected void mutate(ContactCreatedEvent event) {
        this.id = requireNonNull(event.getContactId());
    }

    protected void mutate(NameChangedEvent event) {
        this.id = requireNonNull(event.getContactId());
        this.firstName = requireNonNull(event.getFirstName());
        this.lastName = requireNonNull(event.getLastName());
    }

    @Override
    protected void mutate(DomainEvent event) {
        if (event instanceof ContactCreatedEvent) {
            mutate((ContactCreatedEvent) event);
        } else if (event instanceof NameChangedEvent) {
            mutate((NameChangedEvent) event);
        }
    }

    public Contact setName(String firstName, String lastName) {
        apply(new NameChangedEvent(id, firstName, lastName));
        return this;
    }

    public ContactId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Address getAddress() {
        return address;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ContactId id;
        private String firstName;
        private String lastName;

        public Contact build() {
            return new Contact(id, firstName, lastName);
        }

        public Builder id(ContactId id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
    }
}
