package org.jaun.clubmanager.domain.model.commons;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventIdTest {

    @Test
    void generate() {
        EventId eventId = EventId.generate();

        assertNotNull(eventId);
        assertNotNull(eventId.getUuid());
        assertThat(eventId, equalTo(eventId));
    }

    @Test
    void generate_eachIsDifferent() {
        EventId eventId1 = EventId.generate();
        EventId eventId2 = EventId.generate();

        assertThat(eventId1, not(equalTo(eventId2)));
    }

    @Test
    void parse() {
        String uuidString = "db716b99-e2db-4541-95a6-e31e54116c08";
        EventId parsedEventId = EventId.parse(uuidString);

        assertThat(parsedEventId.getValue(), equalTo(uuidString));
        assertThat(parsedEventId.getUuid(), equalTo(UUID.fromString(uuidString)));
    }

    @Test
    void parse_nok() {
        String uuidString = "123";
        Executable e = () -> EventId.parse(uuidString);

        assertThrows(IllegalArgumentException.class, e);
    }
}