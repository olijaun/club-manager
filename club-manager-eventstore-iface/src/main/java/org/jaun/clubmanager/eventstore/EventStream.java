package org.jaun.clubmanager.eventstore;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;

import static java.util.Objects.requireNonNull;

public class EventStream<E extends DomainEvent> {
    private final List<E> eventList;
    private final StreamId streamId;

    public EventStream(StreamId streamId, List<E> eventList) {
        this.streamId = requireNonNull(streamId);
        this.eventList = requireNonNull(eventList);
    }

    public List<E> getEventList() {
        return eventList;
    }

    public StreamId getStreamId() {
        return streamId;
    }

    public int size() {
        return eventList.size();
    }

    public boolean isEmpty() {
        return eventList.isEmpty();
    }

    public Stream<E> stream() {
        return eventList.stream();
    }

    public int getCurrentVersion() {
        return size() - 1;
    }
}
