package org.jaun.clubmanager.person.domain.model.person;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PersonIdTest {

    @Test
    void construct_ok() {
        PersonId personId = new PersonId("P12345678");

        assertThat(personId.getValue(), equalTo("P12345678"));
        assertThat(personId.asInt(), equalTo(12345678));
    }

    @Test
    void construct_invalid() {
        Executable e0 = () -> new PersonId(null);
        Executable e1 = () -> new PersonId("Q12345678");
        Executable e2 = () -> new PersonId("12345678");
        Executable e3 = () -> new PersonId("P1");
        Executable e4 = () -> new PersonId("P123456789");

        assertThrows(NullPointerException.class, e0);
        assertThrows(IllegalArgumentException.class, e1);
        assertThrows(IllegalArgumentException.class, e2);
        assertThrows(IllegalArgumentException.class, e3);
        assertThrows(IllegalArgumentException.class, e4);
    }
}