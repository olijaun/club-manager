package org.jaun.clubmanager.person.domain.model.person;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PhoneNumberTest {

    @Test
    void nullCheck() {

        Executable e = () ->  new PhoneNumber(null, new Locale("de", "CH"));

        assertThrows(NullPointerException.class, e);
    }

    @Test
    void valid_swiss() {

        PhoneNumber phoneNumber = new PhoneNumber("0789991122", new Locale("de", "CH"));

        assertThat(phoneNumber.getValue(), equalTo("0789991122"));
    }

    @Test
    void valid_german() {

        PhoneNumber phoneNumber = new PhoneNumber("03012345678", new Locale("de", "DE"));

        assertThat(phoneNumber.getValue(), equalTo("03012345678"));
    }

    @Test
    void invalid() {

        Executable e = () -> new PhoneNumber("1", new Locale("de", "CH"));

        assertThrows(IllegalArgumentException.class, e);
    }

    @Test
    void getInternationalFormat_swiss() {

        PhoneNumber phoneNumber = new PhoneNumber("0789991122", new Locale("de", "CH"));

        assertThat(phoneNumber.getInternationalFormat(), equalTo("+41 78 999 11 22"));
    }

    @Test
    void getInternationalFormat_german() {

        PhoneNumber phoneNumber = new PhoneNumber("03012345678", new Locale("de", "DE"));

        assertThat(phoneNumber.getInternationalFormat(), equalTo("+49 30 12345678"));
    }
}