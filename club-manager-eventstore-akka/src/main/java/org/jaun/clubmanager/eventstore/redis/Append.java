package org.jaun.clubmanager.eventstore.redis;

import java.io.Serializable;
import java.util.List;

import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.StreamRevision;

public class Append implements Serializable {
    private final List<EventData> events;
    private final StreamRevision expectedVersion;

    public Append(List<EventData> events, StreamRevision expectedVersion) {
        this.events = events;
        this.expectedVersion = expectedVersion;
    }

    public List<EventData> getEvents() {
        return events;
    }

    public StreamRevision getExpectedVersion() {
        return expectedVersion;
    }
}
