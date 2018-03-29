package org.jaun.clubmanager.member.domain.model.contact;

import java.time.LocalDate;
import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactCreatedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.PersonNameChangedEvent;

public class Person extends Contact {

    private PersonName personName;
    private Sex sex;
    private LocalDate birthDate;

    public Person(ContactId id, PersonName personName) {
        super(id, ContactType.PERSON);
        changeName(personName);
    }

    public Person(EventStream<Contact> eventStream) {
        super(eventStream);
    }

    protected void mutate(PersonNameChangedEvent event) {
        this.personName =
                new PersonName(event.getFirstName().orElse(null), event.getMiddleName().orElse(null), event.getLastName());
    }

    @Override
    protected void mutate(DomainEvent event) {
        if (event instanceof ContactCreatedEvent) {
            mutate((ContactCreatedEvent) event);
        } else if (event instanceof PersonNameChangedEvent) {
            mutate((PersonNameChangedEvent) event);
        }
    }

    public void changeName(PersonName newName) {

        if (newName.equals(personName)) {
            return;
        }

        apply(new PersonNameChangedEvent(getId(), newName.getFirstName().orElse(null), newName.getMiddleName().orElse(null),
                newName.getLastName()));
    }

    @Override
    public PersonName getName() {
        return personName;
    }

    public Optional<Sex> getSex() {
        return Optional.ofNullable(sex);
    }

    public Optional<LocalDate> getBirthDate() {
        return Optional.ofNullable(birthDate);
    }

//    public static Builder builder() {
//        return new Builder();
//    }

//    public static class Builder {
//        private ContactId id;
//        private String firstName;
//        private String lastName;
//
//        public Person build() {
//            return new Person(id, firstName, lastName);
//        }
//
//        public Builder id(ContactId id) {
//            this.id = id;
//            return this;
//        }
//
//        public Builder firstName(String firstName) {
//            this.firstName = firstName;
//            return this;
//        }
//
//        public Builder lastName(String lastName) {
//            this.lastName = lastName;
//            return this;
//        }
//    }
}
