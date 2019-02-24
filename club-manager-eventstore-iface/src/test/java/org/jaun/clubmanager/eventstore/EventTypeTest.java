package org.jaun.clubmanager.eventstore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventTypeTest {

    @Test
    void construct_ok() {
        String typeName = "abc";
        EventType eventType = new EventType(typeName);

        assertThat(eventType.getValue(), equalTo(typeName));
    }

    @Test
    void construct_null() {
        Executable e = () -> new EventType(null);

        assertThrows(NullPointerException.class, e);
    }
}