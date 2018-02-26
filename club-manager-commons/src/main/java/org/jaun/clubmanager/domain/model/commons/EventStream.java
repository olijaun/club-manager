package org.jaun.clubmanager.domain.model.commons;

import java.util.List;
import java.util.stream.Stream;

public class EventStream<T extends Aggregate<?>> {
    private final List<DomainEvent> eventList;
    private final StreamId streamId;

    public EventStream(StreamId streamId, List<DomainEvent> eventList) {
        this.eventList = eventList;
        this.streamId = streamId;
    }

    public List<DomainEvent> getEventList() {
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

    public Stream<DomainEvent> stream() {
        return eventList.stream();
    }

    public int getCurrentVersion() {
        return size() - 1;
    }
}
