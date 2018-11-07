package org.jaun.clubmanager.eventstore.akka;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.StreamRevision;

import com.google.common.collect.ImmutableList;

public class Append implements Serializable {
    private final List<EventData> events;
    private final StreamRevision expectedVersion;

    public Append(List<EventData> events, StreamRevision expectedVersion) {
        this.events = ImmutableList.copyOf(events);
        this.expectedVersion = Objects.requireNonNull(expectedVersion);
    }

    public List<EventData> getEvents() {
        return events;
    }

    public StreamRevision getExpectedVersion() {
        return expectedVersion;
    }
}
