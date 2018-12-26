package org.jaun.clubmanager.domain.model.commons;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IdTest {

    @Test
    void getValue() {
        Id id = new Id("abc") {

        };

        assertThat(id.getValue(), equalTo("abc"));
    }

    @Test
    void nullValue() {
        Executable e = () -> new Id(null) {

        };

        assertThrows(NullPointerException.class, e);
    }

    class TestId extends Id {
        TestId(String value) {
            super(value);
        }
    }

    @Test
    void random() {
        TestId id = TestId.random(TestId::new);

        assertNotNull(id);
        assertThat(id.getValue(), not(equalTo("")));
    }
}