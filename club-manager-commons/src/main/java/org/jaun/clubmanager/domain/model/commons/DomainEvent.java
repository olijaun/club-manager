package org.jaun.clubmanager.domain.model.commons;

import java.io.Serializable;

public class DomainEvent<T extends EventType> extends ValueObject implements Serializable {
    private final EventId eventId;
    private final T eventType;

    public DomainEvent(EventId eventId, T eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
    }

    public EventId getEventId() {
        return eventId;
    }

    public T getEventType() {
        return eventType;
    }
}
