package org.jaun.clubmanager.contact.domain.model.contact;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import org.jaun.clubmanager.contact.domain.model.contact.event.BasicDataChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.ContactDataChangedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.PersonCreatedEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.PersonEvent;
import org.jaun.clubmanager.contact.domain.model.contact.event.StreetAddressChangedEvent;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.eventstore.EventStream;

public class Person extends EventSourcingAggregate<PersonId, PersonEvent> {

    private PersonId id;
    private PersonType personType;
    private Name name;
    private StreetAddress streetAddress;
    private PhoneNumber phoneNumber;
    private EmailAddress emailAddress;
    private Sex sex;
    private LocalDate birthDate;

    public Person(PersonId id, PersonType personType, Name name, LocalDate birthDate, Sex sex) {
        apply(new PersonCreatedEvent(id, personType));
        apply(new BasicDataChangedEvent(id, name, birthDate, sex));
    }

    public Person(EventStream<PersonEvent> eventStream) {
        replayEvents(eventStream);
    }

    protected void mutate(PersonCreatedEvent event) {
        this.id = event.getPersonId();
        this.personType = event.getPersonType();
    }

    protected void mutate(BasicDataChangedEvent event) {
        this.name = event.getName();
        this.sex = event.getSex().orElse(null);
        this.birthDate = event.getBirthDate().orElse(null);
    }

    protected void mutate(ContactDataChangedEvent event) {
        this.emailAddress = event.getEmailAddress().orElse(null);
        this.phoneNumber = event.getPhoneNumber().orElse(null);
    }

    protected void mutate(StreetAddressChangedEvent event) {
        this.streetAddress = event.getStreetAddress().orElse(null);
    }

    @Override
    protected void mutate(PersonEvent event) {
        if (event instanceof PersonCreatedEvent) {
            mutate((PersonCreatedEvent) event);
        } else if (event instanceof BasicDataChangedEvent) {
            mutate((BasicDataChangedEvent) event);
        } else if (event instanceof ContactDataChangedEvent) {
            mutate((ContactDataChangedEvent) event);
        } else if (event instanceof ContactDataChangedEvent) {
            mutate((ContactDataChangedEvent) event);
        } else if (event instanceof StreetAddressChangedEvent) {
            mutate((StreetAddressChangedEvent) event);
        }
    }

    public void changeBasicData(Name newName, LocalDate birthDate, Sex sex) {

        requireNonNull(newName);

        if (this.name.equals(newName)) {
            return;
        }

        if (!personType.equals(PersonType.NATURAL) && sex != null) {
            throw new IllegalStateException("sex can only be defined for a person");
        }

        if (!personType.equals(PersonType.NATURAL) && birthDate != null) {
            throw new IllegalStateException("birth date can only be defined for a person");
        }

        if (Objects.equals(sex, this.sex) && Objects.equals(birthDate, this.birthDate) && Objects.equals(sex, this.sex)) {
            return;
        }

        apply(new BasicDataChangedEvent(id, newName, birthDate, sex));
    }

    public void changeContactData(EmailAddress emailAddress, PhoneNumber phoneNumber) {

        if (Objects.equals(emailAddress, this.emailAddress) && Objects.equals(phoneNumber, this.phoneNumber)) {
            return;
        }

        apply(new ContactDataChangedEvent(id, emailAddress, phoneNumber));
    }

    public void changeStreetAddress(StreetAddress streetAddress) {

        if (Objects.equals(streetAddress, this.streetAddress)) {
            return;
        }

        apply(new StreetAddressChangedEvent(id, streetAddress));
    }

    public PersonId getId() {
        return id;
    }

    public Name getName() {
        return name;
    }

    public StreetAddress getStreetAddress() {
        return streetAddress;
    }

    public Optional<PhoneNumber> getPhoneNumber() {
        return Optional.ofNullable(phoneNumber);
    }

    public Optional<EmailAddress> getEmailAddress() {
        return Optional.ofNullable(emailAddress);
    }

    public PersonType getPersonType() {
        return personType;
    }

    public Optional<Sex> getSex() {
        return Optional.ofNullable(sex);
    }

    public Optional<LocalDate> getBirthDate() {
        return Optional.ofNullable(birthDate);
    }

    public static abstract class Builder {
        private PersonId id;
        private PersonType personType;
        private Name name;
        private StreetAddress streetAddress;
        private PhoneNumber phone;
        private EmailAddress emailAddress;

        public abstract void build();

        public Builder id(PersonId id) {
            this.id = id;
            return this;
        }

        public Builder contactType(PersonType personType) {
            this.personType = personType;
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
