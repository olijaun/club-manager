package org.jaun.clubmanager.person.domain.model.person;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class NameTest {

    @Test
    void construct_ok() {
        Name name = new Name("de Lucia", "Paco");

        assertThat(name.getLastNameOrCompanyName(), equalTo("de Lucia"));
        assertThat(name.getFirstName().get(), equalTo("Paco"));
    }

    @Test
    void construct_null() {
        Executable e = () -> new Name(null, "Paco");

        Assertions.assertThrows(NullPointerException.class, e);
    }
}