package org.jaun.clubmanager.eventstore.akka;

import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.jaun.clubmanager.domain.model.commons.EventId;
import org.jaun.clubmanager.eventstore.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AkkaEventStoreTest {

    private static ActorSystem actorSystem;
    private AkkaEventStore store;

    @BeforeAll
    public static void setup() {
        actorSystem = ActorSystem.create();
    }

    @AfterAll
    public static void teardown() {
        TestKit.shutdownActorSystem(actorSystem);
        actorSystem = null;
    }

    @BeforeEach
    public void setUp() {
        store = new AkkaEventStore(actorSystem);
    }

    @Test
    void append_eventWithoutMetadata() throws Exception {

        // prepare
        StreamId streamId = randomStreamId();
        EventData event = EventDataFixture.json().build();

        // run
        store.append(streamId, Collections.singletonList(event), StreamRevision.UNSPECIFIED);

        // verify
        StoredEvents storedEvents = store.read(streamId);
        assertThat(storedEvents.size(), is(1));
        StoredEventData storedEventData = storedEvents.stream().findFirst().get();
        assertThat(storedEventData.getStreamId(), equalTo(streamId));
        assertThat(storedEventData.getEventId(), equalTo(event.getEventId()));
        assertThat(storedEventData.getEventType(), equalTo(event.getEventType()));
        assertThat(storedEventData.getStreamRevision(), equalTo(StreamRevision.from(0)));
        assertThat(storedEventData.getPayload(), equalTo(event.getPayload()));
        assertFalse(storedEventData.getMetadata().isPresent());
    }

    @Test
    void append_eventWithMetadata() throws Exception {

        // prepare
        StreamId streamId = randomStreamId();
        EventData event = EventDataFixture.jsonWithMetadata().build();

        // run
        store.append(streamId, Collections.singletonList(event), StreamRevision.UNSPECIFIED);

        // verify
        StoredEvents storedEvents = store.read(streamId);
        assertThat(storedEvents.size(), is(1));
        StoredEventData storedEventData = storedEvents.stream().findFirst().get();
        assertThat(storedEventData.getStreamId(), equalTo(streamId));
        assertThat(storedEventData.getEventId(), equalTo(event.getEventId()));
        assertThat(storedEventData.getEventType(), equalTo(event.getEventType()));
        assertThat(storedEventData.getStreamRevision(), equalTo(StreamRevision.from(0)));
        assertThat(storedEventData.getPayload(), equalTo(event.getPayload()));
        assertThat(storedEventData.getMetadata().get(), equalTo(event.getMetadata().get()));
    }

    @Test
    void append_appendToNewStream() throws Exception {

        // prepare
        StreamId streamId = randomStreamId();
        EventData event = EventDataFixture.jsonWithMetadata().build();

        // run
        store.append(streamId, Collections.singletonList(event), StreamRevision.NEW_STREAM);

        // verify
        StoredEvents storedEvents = store.read(streamId);
        assertThat(storedEvents.size(), is(1));
    }

    @Test
    void append_appendToExistingStream() throws Exception {

        // prepare
        StreamId streamId = randomStreamId();
        EventData event1 = EventDataFixture.jsonWithMetadata().build();
        EventData event2 = EventDataFixture.jsonWithMetadata().build();

        store.append(streamId, Collections.singletonList(event1), StreamRevision.NEW_STREAM);

        // run
        store.append(streamId, Collections.singletonList(event2), StreamRevision.from(0));

        // verify
        StoredEvents storedEvents = store.read(streamId);
        assertThat(storedEvents.size(), is(2));

        List<StoredEventData> storedEventDataList = storedEvents.stream().collect(Collectors.toList());

        assertThat(storedEventDataList.get(0).getEventId(), equalTo(event1.getEventId()));
        assertThat(storedEventDataList.get(1).getEventId(), equalTo(event2.getEventId()));
    }


    @Test
    void append_appendToNewStream_alreadyExists() throws Exception {

        // prepare
        StreamId streamId = randomStreamId();
        EventData event1 = EventDataFixture.jsonWithMetadata().build();
        EventData event2 = EventDataFixture.jsonWithMetadata().build();

        store.append(streamId, Collections.singletonList(event1), StreamRevision.NEW_STREAM);

        // run
        Executable e = () -> store.append(streamId, Collections.singletonList(event2), StreamRevision.NEW_STREAM);

        // verify
        StoredEvents storedEvents = store.read(streamId);
        assertThat(storedEvents.size(), is(1));

        ConcurrencyException concurrencyException = assertThrows(ConcurrencyException.class, e);
        assertThat(concurrencyException.getExpectedVersion(), equalTo(StreamRevision.NEW_STREAM.getValue()));
        assertThat(concurrencyException.getActualVersion(), equalTo(StreamRevision.from(0).getValue()));
    }

    @Test
    void append_append_expectedVersionDoesNotMatch() throws Exception {

        // prepare
        StreamId streamId = randomStreamId();
        EventData event1 = EventDataFixture.jsonWithMetadata().build();
        EventData event2 = EventDataFixture.jsonWithMetadata().build();
        StreamRevision expectedRevision = StreamRevision.from(42);
        store.append(streamId, Collections.singletonList(event1), StreamRevision.NEW_STREAM);

        // run
        Executable e = () -> store.append(streamId, Collections.singletonList(event2), expectedRevision);

        // verify
        StoredEvents storedEvents = store.read(streamId);
        assertThat(storedEvents.size(), is(1));

        ConcurrencyException concurrencyException = assertThrows(ConcurrencyException.class, e);
        assertThat(concurrencyException.getExpectedVersion(), equalTo(expectedRevision.getValue()));
        assertThat(concurrencyException.getActualVersion(), equalTo(StreamRevision.from(0).getValue()));
    }

    @Test
    void read_emptyStream() throws Exception {

        // prepare
        StreamId streamId = randomStreamId();

        // run
        Executable e = () -> store.read(streamId);

        // verify
        StreamNotFoundException streamNotFoundException = assertThrows(StreamNotFoundException.class, e);
        assertThat(streamNotFoundException.getStreamId(), equalTo(streamId));
    }

    @Test
    void read() throws Exception {
        // prepare
        StreamId streamId = randomStreamId();
        EventData event = EventDataFixture.json().build();
        store.append(streamId, Collections.singletonList(event), StreamRevision.UNSPECIFIED);

        // run
        StoredEvents storedEvents = store.read(streamId);

        // verify
        assertThat(storedEvents.size(), is(1));
    }

    @Test
    void read_fromInitialRevision() throws Exception {
        // prepare
        StreamId streamId = randomStreamId();
        EventData event1 = EventDataFixture.json().build();
        EventData event2 = EventDataFixture.json().build();
        store.append(streamId, Collections.singletonList(event1), StreamRevision.UNSPECIFIED);
        store.append(streamId, Collections.singletonList(event2), StreamRevision.UNSPECIFIED);

        // run
        StoredEvents storedEvents = store.read(streamId, StreamRevision.INITIAL);

        // verify
        assertThat(storedEvents.size(), is(2));

        List<StoredEventData> storedEventDataList = storedEvents.stream().collect(Collectors.toList());
        assertThat(storedEventDataList.get(0).getEventId(), equalTo(event1.getEventId()));
        assertThat(storedEventDataList.get(1).getEventId(), equalTo(event2.getEventId()));
    }

    @Test
    void read_fromRevision() throws Exception {
        // prepare
        StreamId streamId = randomStreamId();
        EventData event1 = EventDataFixture.json().build();
        EventData event2 = EventDataFixture.json().build();
        store.append(streamId, Collections.singletonList(event1), StreamRevision.UNSPECIFIED);
        store.append(streamId, Collections.singletonList(event2), StreamRevision.UNSPECIFIED);

        // run
        StoredEvents storedEvents = store.read(streamId, StreamRevision.from(1));

        // verify
        assertThat(storedEvents.size(), is(1));
        List<StoredEventData> storedEventDataList = storedEvents.stream().collect(Collectors.toList());
        assertThat(storedEventDataList.get(0).getEventId(), equalTo(event2.getEventId()));
    }

    @Test
    void read_fromInitialRevisionToMax() throws Exception {
        // prepare
        StreamId streamId = randomStreamId();
        EventData event1 = EventDataFixture.json().build();
        EventData event2 = EventDataFixture.json().build();
        store.append(streamId, Collections.singletonList(event1), StreamRevision.UNSPECIFIED);
        store.append(streamId, Collections.singletonList(event2), StreamRevision.UNSPECIFIED);

        // run
        StoredEvents storedEvents = store.read(streamId, StreamRevision.INITIAL, StreamRevision.MAXIMUM);

        // verify
        assertThat(storedEvents.size(), is(2));

        List<StoredEventData> storedEventDataList = storedEvents.stream().collect(Collectors.toList());
        assertThat(storedEventDataList.get(0).getEventId(), equalTo(event1.getEventId()));
        assertThat(storedEventDataList.get(1).getEventId(), equalTo(event2.getEventId()));
    }

    @Test
    void read_fromInitialRevisionToFirst() throws Exception {
        // prepare
        StreamId streamId = randomStreamId();
        EventData event1 = EventDataFixture.json().build();
        EventData event2 = EventDataFixture.json().build();
        store.append(streamId, Collections.singletonList(event1), StreamRevision.UNSPECIFIED);
        store.append(streamId, Collections.singletonList(event2), StreamRevision.UNSPECIFIED);

        // run
        StoredEvents storedEvents = store.read(streamId, StreamRevision.INITIAL, StreamRevision.from(0));

        // verify
        assertThat(storedEvents.size(), is(1));

        List<StoredEventData> storedEventDataList = storedEvents.stream().collect(Collectors.toList());
        assertThat(storedEventDataList.get(0).getEventId(), equalTo(event1.getEventId()));
    }

    private StreamId randomStreamId() {
        return StreamId.parse("teststream-" + UUID.randomUUID().toString());
    }
}