package org.jaun.clubmanager.eventstore.akka;

import org.hamcrest.CoreMatchers;
import org.jaun.clubmanager.domain.model.commons.EventId;
import org.jaun.clubmanager.domain.model.commons.Id;
import org.jaun.clubmanager.eventstore.Category;
import org.jaun.clubmanager.eventstore.EventDataFixture;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StreamId;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JsonSerializerTest {

    private JsonSerializer jsonSerializer;

    @BeforeEach
    void setUp() {
        this.jsonSerializer = new JsonSerializer();
    }

    @Test
    void manifest() {

        // prepare
        EventDataWithStreamId eventDataWithStreamId = EventDataWithStreamIdFixture.eventDataWithStreamId();

        // run
        String manifest = jsonSerializer.manifest(eventDataWithStreamId);

        // verify
        assertThat(manifest, equalTo(eventDataWithStreamId.getEventType().getValue()));
    }

    @Test
    void manifest_unsupportedType() {

        // prepare
        Object o = new Object();

        // run
        Executable e = () -> jsonSerializer.manifest(o);

        // verify
        assertThrows(IllegalArgumentException.class, e);
    }

    @Test
    void identifier() {

        // run
        int identifier = jsonSerializer.identifier();

        // verify
        assertThat(identifier, is(31415));
    }

    @Test
    void toBinary() {

        // prepare
        EventId eventId = new EventId(UUID.fromString("7fe5c6db-0513-452d-ac9a-99ae3c530b0e"));
        EventDataWithStreamId eventDataWithStreamId = new EventDataWithStreamId(StreamId.parse("tobinarystream-1"), EventDataFixture.jsonWithMetadata(eventId).build());

        // run
        byte[] bytes = jsonSerializer.toBinary(eventDataWithStreamId);

        // verify
        String expectedResult = "{\"eventId\":\"7fe5c6db-0513-452d-ac9a-99ae3c530b0e\",\"eventType\":\"typeA\",\"streamId\":\"tobinarystream-1\",\"metadata\":{\"meta\":\"data\"},\"data\":{\"hello\":\"world\"}}";
        String actualResult = new String(bytes, StandardCharsets.UTF_8);

        assertThat(actualResult, equalTo(expectedResult));
    }

    @Test
    void fromBinary() throws Exception {
        // prepare
        String eventAsString = "{\"eventId\":\"7fe5c6db-0513-452d-ac9a-99ae3c530b0e\",\"eventType\":\"typeA\",\"streamId\":\"tobinarystream-1\",\"metadata\":{\"meta\":\"data\"},\"data\":{\"hello\":\"world\"}}";
        byte[] serializedEvent = eventAsString.getBytes(StandardCharsets.UTF_8);


        // run
        EventDataWithStreamId deserializedEventDataWithStreamId = (EventDataWithStreamId) jsonSerializer.fromBinary(serializedEvent, "bla");

        // verify

        EventId eventId = new EventId(UUID.fromString("7fe5c6db-0513-452d-ac9a-99ae3c530b0e"));
        EventDataWithStreamId eventDataWithStreamId = new EventDataWithStreamId(StreamId.parse("tobinarystream-1"), EventDataFixture.jsonWithMetadata(eventId).build());

        assertThat(deserializedEventDataWithStreamId, equalTo(eventDataWithStreamId));
    }
}