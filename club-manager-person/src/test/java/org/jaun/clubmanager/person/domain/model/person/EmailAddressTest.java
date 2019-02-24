package org.jaun.clubmanager.person.domain.model.person;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.*;

class EmailAddressTest {

    @Test
    void valid() {
        EmailAddress emailAddress1 = new EmailAddress("abc@def.ch");
        EmailAddress emailAddress2 = new EmailAddress("abc.abc@def.hij.com");
        EmailAddress emailAddress3 = new EmailAddress("abc-def@def-hij.org");
    }

    @Test
    void invalid() {
        Executable e0 = () -> new EmailAddress(null);
        Executable e1 = () -> new EmailAddress("abcdef.ch");
        Executable e2 = () -> new EmailAddress("abc@def");
        Executable e3 = () -> new EmailAddress("abc@@def.ch");

        assertThrows(NullPointerException.class, e0);
        assertThrows(IllegalArgumentException.class, e1);
        assertThrows(IllegalArgumentException.class, e2);
        assertThrows(IllegalArgumentException.class, e3);
    }
}