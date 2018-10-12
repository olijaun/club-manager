package org.jaun.clubmanager.member.domain.model.person;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.jaun.clubmanager.person.domain.model.person.Country;
import org.junit.Test;

public class CountryTest {

    @Test
    public void construct() {
        Country country = new Country("CH");

        assertThat(country.getIsoCountryCode(), equalTo("CH"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void construct_invalid() {
        new Country("XX");
    }
}