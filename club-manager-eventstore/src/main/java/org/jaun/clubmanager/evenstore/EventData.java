package org.jaun.clubmanager.evenstore;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.google.common.base.MoreObjects;

/**
 * Event data. Containing the deserialized event.
 */
public class EventData {

    private final EventId eventId;
    private final String payload;
    private final String metadata;
    private final long streamVersion;

    /**
     * @param eventId
     * @param payload
     *         The actual event serialized into a string (could be a JSON).
     * @param metadata
     */
    public EventData(EventId eventId, String payload, String metadata, long streamVersion) {
        this.eventId = requireNonNull(eventId);
        this.payload = requireNonNull(payload);
        this.metadata = requireNonNull(metadata);
        this.streamVersion = streamVersion;
    }

    public EventId getEventId() {
        return eventId;
    }

    public String getPayload() {
        return payload;
    }

    public String getMetadata() {
        return metadata;
    }

    public long getStreamVersion() {
        return streamVersion;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("id", eventId.getUuid().toString()).add("metadata", metadata).toString();
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
}
