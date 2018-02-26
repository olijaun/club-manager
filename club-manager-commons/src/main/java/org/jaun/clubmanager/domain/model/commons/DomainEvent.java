package org.jaun.clubmanager.domain.model.commons;

import java.io.Serializable;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class DomainEvent<T extends EventType> extends ValueObject implements Serializable {
    private final EventId eventId;
    private final T eventType;

    public DomainEvent(EventId eventId, T eventType) {
        this.eventId = requireNonNull(eventId);
        this.eventType = requireNonNull(eventType);
    }

    public DomainEvent(T eventType) {
        this(EventId.generate(), eventType);
    }

    public EventId getEventId() {
        return eventId;
    }

    public T getEventType() {
        return eventType;
    }
}
