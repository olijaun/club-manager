package org.jaun.clubmanager.eventstore;

import org.jaun.clubmanager.domain.model.commons.EventId;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class StoredEventDataFixture {

    public static StoredEventData.Builder storedEventData() {

        StreamId streamId = StreamId.parse("test-123");
        EventId eventId = new EventId(UUID.fromString("615f1e7c-53e3-4b6b-8a69-65b15758a3db"));
        EventType eventType = new EventType("typeA");
        String payload = "{\"hello\":\"world\"}";
        String metadata = "{\"meta\":\"data\"}";
        Instant instant = ZonedDateTime.of(2018, 12, 24, 0, 0, 0, 0, ZoneId.systemDefault()).toInstant();

        return new StoredEventData.Builder()
                .streamId(streamId)
                .eventId(eventId)
                .eventType(eventType)
                .payload(payload)
                .metadata(metadata)
                .streamRevision(StreamRevision.NEW_STREAM)
                .timestamp(instant)
                .position(0);
    }

}
