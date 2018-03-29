package org.jaun.clubmanager.member.domain.model.contact;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.contact.event.CompanyNameChangedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactCreatedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.NameChangedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.PersonNameChangedEvent;

public class Company extends Contact {

    private CompanyName companyName;

    public Company(ContactId id) {
        super(id, ContactType.PERSON);
        changeName(companyName);
    }

    public Company(EventStream<Contact> eventStream) {
        super(eventStream);
    }

    protected void mutate(CompanyNameChangedEvent event) {
        this.companyName = new CompanyName(event.getName());
    }

    @Override
    protected void mutate(DomainEvent event) {
        if (event instanceof ContactCreatedEvent) {
            mutate((ContactCreatedEvent) event);
        } else if (event instanceof NameChangedEvent) {
            mutate((CompanyNameChangedEvent) event);
        }
    }

    public void changeName(CompanyName newName) {

        if(newName.equals(companyName)) {
            return;
        }

        apply(new CompanyNameChangedEvent(getId(), newName.getName()));
    }

    public CompanyName getName() {
        return companyName;
    }

//    public static abstract class Builder {
//        private ContactId id;
//        private ContactType contactType;
//        private Name name;
//        private Address address;
//        private PhoneNumber phone;
//        private EmailAddress emailAddress;
//
//        public abstract void build();
//
//        public Builder id(ContactId id) {
//            this.id = id;
//            return this;
//        }
//
//        public Builder contactType(ContactType contactType) {
//            this.contactType = contactType;
//            return this;
//        }
//
//        public Builder name(Name name) {
//            this.name = name;
//            return this;
//        }
//
//        public Builder address(Address address) {
//            this.address = address;
//            return this;
//        }
//
//        public Builder phone(PhoneNumber phone) {
//            this.phone = phone;
//            return this;
//        }
//
//        public Builder emailAddress(EmailAddress emailAddress) {
//            this.emailAddress = emailAddress;
//            return this;
//        }
//    }
}
