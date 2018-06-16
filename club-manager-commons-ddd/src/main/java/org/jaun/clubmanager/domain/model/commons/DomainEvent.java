package org.jaun.clubmanager.domain.model.commons;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DomainEvent extends ValueObject implements Serializable {
    private final EventId eventId;

    public DomainEvent(EventId eventId) {
        this.eventId = requireNonNull(eventId);
    }

    public EventId getEventId() {
        return eventId;
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
