package org.jaun.clubmanager.eventstore.akka;

public class EventStored {
    private final String eventId;

    public EventStored(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }
}
