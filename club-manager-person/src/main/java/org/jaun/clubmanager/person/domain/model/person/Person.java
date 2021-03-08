package org.jaun.clubmanager.person.domain.model.person;

import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.person.domain.model.person.event.*;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class Person extends EventSourcingAggregate<PersonId, PersonEvent> {

    private PersonId id;
    private PersonType personType;
    private Name name;
    private StreetAddress streetAddress;
    private PhoneNumber phoneNumber;
    private EmailAddress emailAddress;
    private Gender gender;
    private LocalDate birthDate;

    public Person(Builder builder) {
        apply(new PersonCreatedEvent(builder.id, builder.personType));
        apply(new BasicDataChangedEvent(builder.id, builder.name, builder.birthDate, builder.gender));
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
        this.gender = event.getGender().orElse(null);
        this.birthDate = event.getBirthDate().orElse(null);
    }

    protected void mutate(ContactDataChangedEvent event) {
        this.emailAddress = event.getEmailAddress().orElse(null);
        this.phoneNumber = event.getPhoneNumber().orElse(null);
    }

    protected void mutate(StreetAddressChangedEvent event) {
        this.streetAddress = event.getStreetAddress();
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

    public void changeBasicData(Name newName, LocalDate birthDate, Gender gender) {

        requireNonNull(newName);

        if (!personType.equals(PersonType.NATURAL) && gender != null) {
            throw new IllegalStateException("gender can only be defined for a person");
        }

        if (!personType.equals(PersonType.NATURAL) && birthDate != null) {
            throw new IllegalStateException("birth date can only be defined for a person");
        }

        if (Objects.equals(newName, this.name) && Objects.equals(gender, this.gender) && Objects.equals(birthDate, this.birthDate)
                && Objects.equals(gender, this.gender)) {
            return;
        }

        apply(new BasicDataChangedEvent(id, newName, birthDate, gender));
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

    public Optional<StreetAddress> getStreetAddress() {
        return Optional.ofNullable(streetAddress);
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

    public Optional<Gender> getGender() {
        return Optional.ofNullable(gender);
    }

    public Optional<LocalDate> getBirthDate() {
        return Optional.ofNullable(birthDate);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private PersonId id;
        private PersonType personType;
        private Name name;
        private Gender gender;
        private StreetAddress streetAddress;
        private PhoneNumber phone;
        private EmailAddress emailAddress;
        private LocalDate birthDate;

        public Person build() {
            return new Person(this);
        }

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

        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public Builder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public Builder personType(PersonType personType) {
            this.personType = personType;
            return this;
        }
    }
}
