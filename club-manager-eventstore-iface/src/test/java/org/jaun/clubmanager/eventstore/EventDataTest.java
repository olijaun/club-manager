package org.jaun.clubmanager.eventstore;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.junit.jupiter.api.Assertions.assertThrows;

class EventDataTest {

    @Test
    void builder_ok() {
        EventDataFixture.json().build();
    }

    @Test
    void builder_null() {
        Executable e1 = () -> EventDataFixture.json()
                .eventId(null).build();

        Executable e2 = () -> EventDataFixture.json()
                .eventType(null).build();

        Executable e3 = () -> EventDataFixture.json()
                .payload(null).build();

        // metadata can be null
        EventDataFixture.json()
                .metadata(null).build();

        assertThrows(NullPointerException.class, e1);
        assertThrows(NullPointerException.class, e2);
        assertThrows(NullPointerException.class, e3);
    }
}