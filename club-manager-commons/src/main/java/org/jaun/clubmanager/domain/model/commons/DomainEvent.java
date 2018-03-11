package org.jaun.clubmanager.domain.model.commons;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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

    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
