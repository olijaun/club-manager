package org.jaun.clubmanager.eventstore.akka;

import java.io.Serializable;
import java.util.List;

public class Append implements Serializable {
    private final List<Event> events;
    private final int expectedVersion;

    public Append(List<Event> events, int expectedVersion) {
        this.events = events;
        this.expectedVersion = expectedVersion;
    }

    public List<Event> getEvents() {
        return events;
    }

    public int getExpectedVersion() {
        return expectedVersion;
    }
}
