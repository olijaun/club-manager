package org.jaun.clubmanager.person.domain.model.person;

import org.jaun.clubmanager.person.domain.model.person.event.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonTest {

    @Test
    void construct() {

        // run
        Person person = PersonFixture.newPerson().build();

        // verify values have been applied correctly to aggregate root
        assertThat(person.getId().getValue(), equalTo("P12345678"));
        assertThat(person.getName().getFirstName().get(), equalTo("Paco"));
        assertThat(person.getName().getLastNameOrCompanyName(), equalTo("de Lucia"));
        assertThat(person.getBirthDate().get(), equalTo(LocalDate.of(1947, 12, 21)));
        assertThat(person.getPersonType(), equalTo(PersonType.NATURAL));

        // verify events have been created
        assertThat(person.getChanges().size(), equalTo(2));

        PersonCreatedEvent personCreatedEvent = (PersonCreatedEvent) person.getChanges().get(0);
        assertThat(personCreatedEvent.getPersonId(), equalTo(person.getId()));
        assertThat(personCreatedEvent.getPersonType(), equalTo(person.getPersonType()));

        BasicDataChangedEvent basicDataChangedEvent = (BasicDataChangedEvent) person.getChanges().get(1);
        assertThat(basicDataChangedEvent.getPersonId(), equalTo(person.getId()));
        assertThat(basicDataChangedEvent.getName(), equalTo(person.getName()));
        assertThat(basicDataChangedEvent.getBirthDate(), equalTo(person.getBirthDate()));
        assertThat(basicDataChangedEvent.getGender(), equalTo(person.getGender()));
    }

    @Test
    void construct_null() {

        Executable e1 = () -> PersonFixture.newPerson().id(null).build();
        Executable e2 = () -> PersonFixture.newPerson().name(null).build();
        Executable e3 = () -> PersonFixture.newPerson().personType(null).build();
        assertThrows(NullPointerException.class, e1);
        assertThrows(NullPointerException.class, e2);
        assertThrows(NullPointerException.class, e3);
    }

    @Test
    void changeBasicData() {
        // prepare
        Person person = PersonFixture.newPerson().build();
        person.clearChanges();

        Name newName = new Name("last", "first");
        LocalDate newBirthDate = LocalDate.of(2000, 1, 1);
        Gender newGender = Gender.FEMALE;

        // run
        person.changeBasicData(newName, newBirthDate, newGender);

        // verify values have been applied correctly to aggregate root
        assertThat(person.getId().getValue(), equalTo("P12345678"));
        assertThat(person.getName().getFirstName().get(), equalTo(newName.getFirstName().get()));
        assertThat(person.getName().getLastNameOrCompanyName(), equalTo(newName.getLastNameOrCompanyName()));
        assertThat(person.getBirthDate().get(), equalTo(newBirthDate));
        assertThat(person.getGender().get(), equalTo(newGender));

        // verify event has been created
        assertThat(person.getChanges().size(), equalTo(1));

        BasicDataChangedEvent basicDataChangedEvent = (BasicDataChangedEvent) person.getChanges().get(0);
        assertThat(basicDataChangedEvent.getPersonId(), equalTo(person.getId()));
        assertThat(basicDataChangedEvent.getName(), equalTo(newName));
        assertThat(basicDataChangedEvent.getBirthDate().get(), equalTo(newBirthDate));
        assertThat(basicDataChangedEvent.getGender().get(), equalTo(newGender));
    }

    @Test
    void changeContactData() {
        // prepare
        Person person = PersonFixture.newPerson().build();
        person.clearChanges();

        EmailAddress newEmailAddress = new EmailAddress("paco@lucia.es");
        PhoneNumber newPhoneNumber = new PhoneNumber("0789999999", Locale.getDefault());

        // run
        person.changeContactData(newEmailAddress, newPhoneNumber);

        // verify values have been applied correctly to aggregate root
        assertThat(person.getEmailAddress().get(), equalTo(newEmailAddress));
        assertThat(person.getPhoneNumber().get(), equalTo(newPhoneNumber));

        // verify event has been created
        assertThat(person.getChanges().size(), equalTo(1));

        ContactDataChangedEvent contactDataChangedEvent = (ContactDataChangedEvent) person.getChanges().get(0);
        assertThat(contactDataChangedEvent.getEmailAddress().get(), equalTo(newEmailAddress));
        assertThat(contactDataChangedEvent.getPhoneNumber().get(), equalTo(newPhoneNumber));
    }

    @Test
    void changeStreetAddress() {

        // prepare
        Person person = PersonFixture.newPerson().build();
        person.clearChanges();

        StreetAddress newStreetAddress = StreetAddress.builder()
                .street("Newstreet")
                .city("Newtown")
                .houseNumber("42")
                .zip("3000")
                .country(new Country("CH"))
                .build();

        // run
        person.changeStreetAddress(newStreetAddress);

        // verify values have been applied correctly to aggregate root

        StreetAddress streetAddress = person.getStreetAddress().get();

        assertThat(streetAddress.getCountry(), equalTo(newStreetAddress.getCountry()));
        assertThat(streetAddress.getCity(), equalTo(newStreetAddress.getCity()));
        assertThat(streetAddress.getStreet(), equalTo(newStreetAddress.getStreet()));
        assertThat(streetAddress.getZip(), equalTo(newStreetAddress.getZip()));
        assertThat(streetAddress.getHouseNumber(), equalTo(newStreetAddress.getHouseNumber()));
        assertFalse(streetAddress.getState().isPresent());

        // verify event has been created
        assertThat(person.getChanges().size(), equalTo(1));

        StreetAddressChangedEvent streetAddressChangedEvent = (StreetAddressChangedEvent) person.getChanges().get(0);
        assertThat(streetAddressChangedEvent.getPersonId(), equalTo(person.getId()));
        assertThat(streetAddressChangedEvent.getStreetAddress().getStreet(), equalTo(newStreetAddress.getStreet()));
        assertThat(streetAddressChangedEvent.getStreetAddress().getHouseNumber(), equalTo(newStreetAddress.getHouseNumber()));
        assertThat(streetAddressChangedEvent.getStreetAddress().getCity(), equalTo(newStreetAddress.getCity()));
        assertThat(streetAddressChangedEvent.getStreetAddress().getZip(), equalTo(newStreetAddress.getZip()));
        assertThat(streetAddressChangedEvent.getStreetAddress().getCountry(), equalTo(newStreetAddress.getCountry()));

    }
}