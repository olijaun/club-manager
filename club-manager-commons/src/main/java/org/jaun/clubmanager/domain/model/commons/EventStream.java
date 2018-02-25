package org.jaun.clubmanager.domain.model.commons;

import java.util.ArrayList;
import java.util.stream.Stream;

public class EventStream<T extends Aggregate<?>> {
    private final ArrayList<DomainEvent> eventList;
    private final StreamId streamId;

    public EventStream(ArrayList<DomainEvent> eventList, StreamId streamId) {
        this.eventList = eventList;
        this.streamId = streamId;
    }

    public ArrayList<DomainEvent> getEventList() {
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
