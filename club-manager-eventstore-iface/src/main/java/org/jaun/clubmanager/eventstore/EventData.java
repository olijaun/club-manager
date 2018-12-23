package org.jaun.clubmanager.eventstore;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.EventId;

import com.google.common.base.MoreObjects;

/**
 * Event data. Containing the deserialized event.
 */
public class EventData implements Serializable {

    private final EventId eventId;
    private final EventType eventType;
    private final String payload;
    private final String metadata;

    private EventData(Builder builder) {
        this.eventId = requireNonNull(builder.eventId);
        this.eventType = requireNonNull(builder.eventType);
        this.payload = requireNonNull(builder.payload);
        this.metadata = builder.metadata;
    }

    public EventData(EventData eventData) {
        this(eventData.getEventId(), eventData.getEventType(), eventData.getPayload(), eventData.getMetadata().orElse(null));
    }

    protected EventData(EventId eventId, EventType eventType, String payload, String metadata) {
        this.eventId = requireNonNull(eventId);
        this.eventType = requireNonNull(eventType);
        this.payload = requireNonNull(payload);
        this.metadata = metadata;
    }

    public EventId getEventId() {
        return eventId;
    }

    public EventType getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public Optional<String> getMetadata() {
        return Optional.ofNullable(metadata);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("id", eventId.getUuid().toString()) //
                .add("eventType", eventType.getValue()) //
                .add("payload", payload) //
                .add("metadata", metadata).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EventData eventData = (EventData) o;
        return Objects.equals(eventId, eventData.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private EventId eventId;
        private EventType eventType;
        private String payload;
        private String metadata;

        public EventData build() {
            return new EventData(this);
        }

        public Builder eventId(EventId eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder eventType(EventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder metadata(String metadata) {
            this.metadata = metadata;
            return this;
        }
    }
}
