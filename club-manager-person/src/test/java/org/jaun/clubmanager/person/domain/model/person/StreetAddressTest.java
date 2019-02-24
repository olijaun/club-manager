package org.jaun.clubmanager.person.domain.model.person;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StreetAddressTest {

    @Test
    void builder_ok() {

        StreetAddress streetAddress = StreetAddressFixture.newStreet().build();

        assertThat(streetAddress.getStreet(), equalTo("Newstreet"));
        assertThat(streetAddress.getHouseNumber().get(), equalTo("42"));
        assertThat(streetAddress.getZip(), equalTo("3000"));
        assertThat(streetAddress.getCity(), equalTo("Newtown"));
        assertThat(streetAddress.getCountry().getIsoCountryCode(), equalTo("CH"));
        assertFalse(streetAddress.getState().isPresent());
    }

    @Test
    void builder_ok_optional() {

        StreetAddress streetAddress = StreetAddressFixture.newStreet().houseNumber(null).state(null).build();

        assertFalse(streetAddress.getHouseNumber().isPresent());
        assertFalse(streetAddress.getState().isPresent());
    }

    @Test
    void builder_null() {

        Executable e1 = () -> StreetAddressFixture.newStreet().street(null).build();
        Executable e2 = () -> StreetAddressFixture.newStreet().zip(null).build();
        Executable e3 = () -> StreetAddressFixture.newStreet().city(null).build();
        Executable e4 = () -> StreetAddressFixture.newStreet().country(null).build();

        assertThrows(NullPointerException.class, e1);
        assertThrows(NullPointerException.class, e2);
        assertThrows(NullPointerException.class, e3);
        assertThrows(NullPointerException.class, e4);
    }
}