package org.jaun.clubmanager.eventstore.redis;

public class EventStored {
    private final String eventId;

    public EventStored(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }
}
