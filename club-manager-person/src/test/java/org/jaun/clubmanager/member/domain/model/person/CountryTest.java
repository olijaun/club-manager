package org.jaun.clubmanager.member.domain.model.person;

import org.jaun.clubmanager.person.domain.model.person.Country;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class CountryTest {

    @Test
    public void construct() {
        Country country = new Country("CH");

        assertThat(country.getIsoCountryCode(), equalTo("CH"));
    }

    @Test
    public void construct_invalid() {
        Executable e = () -> new Country("XX");

        Assertions.assertThrows(IllegalArgumentException.class, e);
    }
}