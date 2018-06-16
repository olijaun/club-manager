package org.jaun.clubmanager.contact.domain.model.contact;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.contact.domain.model.contact.event.BirthDateChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.ContactCreatedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.ContactEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.EmailAddressChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.NameChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.PhoneNumberChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.SexChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.StreetAddressChangedEvent;
import org.jaun.clubmanager.eventstore.EventStream;

public class Contact extends EventSourcingAggregate<ContactId, ContactEvent> {

    private ContactId id;
    private ContactType contactType;
    private Name name;
    private StreetAddress streetAddress;
    private PhoneNumber phoneNumber;
    private EmailAddress emailAddress;
    private Sex sex;
    private LocalDate birthDate;

    public Contact(ContactId id, ContactType contactType, Name name) {
        apply(new ContactCreatedEvent(id, contactType));
        apply(new NameChangedEvent(id, name));
    }

    public Contact(EventStream<ContactEvent> eventStream) {
        replayEvents(eventStream);
    }

    protected void mutate(ContactCreatedEvent event) {
        this.id = event.getContactId();
        this.contactType = event.getContactType();
    }

    protected void mutate(NameChangedEvent event) {
        this.name = event.getName();
    }

    protected void mutate(EmailAddressChangedEvent event) {
        this.emailAddress = event.getEmailAddress().orElse(null);
    }

    protected void mutate(PhoneNumberChangedEvent event) {
        this.phoneNumber = event.getPhoneNumber().orElse(null);
    }

    protected void mutate(SexChangedEvent event) {
        this.sex = event.getSex().orElse(null);
    }

    protected void mutate(StreetAddressChangedEvent event) {
        this.streetAddress = event.getStreetAddress().orElse(null);
    }

    protected void mutate(BirthDateChangedEvent event) {
        this.birthDate = event.getBirthDate().orElse(null);
    }

    @Override
    protected void mutate(ContactEvent event) {
        if (event instanceof ContactCreatedEvent) {
            mutate((ContactCreatedEvent) event);
        } else if (event instanceof NameChangedEvent) {
            mutate((NameChangedEvent) event);
        } else if (event instanceof EmailAddressChangedEvent) {
            mutate((EmailAddressChangedEvent) event);
        } else if (event instanceof PhoneNumberChangedEvent) {
            mutate((PhoneNumberChangedEvent) event);
        } else if (event instanceof SexChangedEvent) {
            mutate((SexChangedEvent) event);
        } else if (event instanceof StreetAddressChangedEvent) {
            mutate((StreetAddressChangedEvent) event);
        } else if (event instanceof BirthDateChangedEvent) {
            mutate((BirthDateChangedEvent) event);
        }
    }

    public void changeName(Name newName) {

        requireNonNull(newName);

        if (this.name.equals(newName)) {
            return;
        }

        apply(new NameChangedEvent(id, newName));
    }

    public void changeSex(Sex sex) {
        if (!contactType.equals(ContactType.PERSON)) {
            throw new IllegalStateException("sex can only be defined for a person");
        }

        if (Objects.equals(sex, this.sex)) {
            return;
        }

        apply(new SexChangedEvent(id, sex));
    }

    public void changeBirthdate(LocalDate birthDate) {
        if (!contactType.equals(ContactType.PERSON)) {
            throw new IllegalStateException("birth date can only be defined for a person");
        }

        if (Objects.equals(birthDate, this.birthDate)) {
            return;
        }

        apply(new BirthDateChangedEvent(id, birthDate));
    }

    public void changeEmailAddress(EmailAddress emailAddress) {

        if (Objects.equals(emailAddress, this.emailAddress)) {
            return;
        }

        apply(new EmailAddressChangedEvent(id, emailAddress));
    }

    public void changePhoneNumber(PhoneNumber phoneNumber) {

        if (Objects.equals(phoneNumber, this.phoneNumber)) {
            return;
        }

        apply(new PhoneNumberChangedEvent(id, phoneNumber));
    }

    public void changeStreetAddress(StreetAddress streetAddress) {

        if (Objects.equals(streetAddress, this.streetAddress)) {
            return;
        }

        apply(new StreetAddressChangedEvent(id, streetAddress));
    }

    public ContactId getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public StreetAddress getStreetAddress() {
        return streetAddress;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public Optional<EmailAddress> getEmailAddress() {
        return Optional.ofNullable(emailAddress);
    }

    public ContactType getContactType() {
        return contactType;
    }

    public Optional<Sex> getSex() {
        return Optional.ofNullable(sex);
    }

    public Optional<LocalDate> getBirthDate() {
        return Optional.ofNullable(birthDate);
    }

    public static abstract class Builder {
        private ContactId id;
        private ContactType contactType;
        private Name name;
        private StreetAddress streetAddress;
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

        public Builder address(StreetAddress streetAddress) {
            this.streetAddress = streetAddress;
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
