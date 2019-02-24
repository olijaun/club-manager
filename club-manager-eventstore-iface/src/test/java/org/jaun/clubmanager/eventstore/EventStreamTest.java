package org.jaun.clubmanager.eventstore;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Collections;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventStreamTest {

    @Test
    void construct_ok() {

        StreamId streamId = StreamId.parse("test-abc");
        DomainEvent domainEvent = new DomainEvent(new EventId(UUID.fromString("d0cfd5f4-3462-439f-b961-77bdd4ed2752")));
        EventStream<DomainEvent> stream = new EventStream<>(streamId, Collections.singletonList(domainEvent));

        assertThat(stream.size(), equalTo(1));
        assertThat(stream.getCurrentVersion(), equalTo(0));
        assertThat(stream.getStreamId(), equalTo(streamId));
        assertThat(stream.getEventList().get(0), equalTo(domainEvent));
        assertThat(stream.isEmpty(), is(false));
    }

    @Test
    void construct_ok_empty() {

        StreamId streamId = StreamId.parse("test-abc");
        EventStream<DomainEvent> stream = new EventStream<>(streamId, Collections.emptyList());

        assertThat(stream.size(), equalTo(0));
        assertThat(stream.getCurrentVersion(), equalTo(-1));
        assertThat(stream.getStreamId(), equalTo(streamId));
        assertThat(stream.isEmpty(), is(true));
    }

    @Test
    void construct_null() {

        StreamId streamId = StreamId.parse("test-abc");
        DomainEvent domainEvent = new DomainEvent(new EventId(UUID.fromString("d0cfd5f4-3462-439f-b961-77bdd4ed2752")));

        Executable e1 = () -> new EventStream<>(null, Collections.singletonList(domainEvent));
        Executable e2 = () -> new EventStream<>(streamId, null);

        assertThrows(NullPointerException.class, e1);
        assertThrows(NullPointerException.class, e2);
    }
}