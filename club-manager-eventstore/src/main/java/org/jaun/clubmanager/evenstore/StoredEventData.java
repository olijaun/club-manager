package org.jaun.clubmanager.evenstore;

import com.google.common.base.MoreObjects;

/**
 * Event data. Containing the deserialized event.
 */
public class StoredEventData extends EventData {

    private final long streamVersion;

    /**
     * @param eventId
     * @param payload
     *         The actual event serialized into a string (could be a JSON).
     * @param metadata
     */
    public StoredEventData(EventId eventId, EventType eventType, String payload, String metadata, long streamVersion) {
        super(eventId, eventType, payload, metadata);
        this.streamVersion = streamVersion;
    }

    public long getStreamVersion() {
        return streamVersion;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("id", getEventId().getUuid().toString()) //
                .add("eventType", getEventType().getValue()) //
                .add("payload", getPayload()) //
                .add("metadata", getMetadata()) //
                .add("streamVersion", streamVersion).toString();
    }
}
