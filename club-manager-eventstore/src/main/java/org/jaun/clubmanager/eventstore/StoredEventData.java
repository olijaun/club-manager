package org.jaun.clubmanager.eventstore;

import com.google.common.base.MoreObjects;

/**
 * Event data. Containing the deserialized event.
 */
public class StoredEventData extends EventData {

    private final StreamRevision streamRevision;

    /**
     * @param eventId
     * @param payload
     *         The actual event serialized into a string (could be a JSON).
     * @param metadata
     */
    public StoredEventData(EventId eventId, EventType eventType, String payload, String metadata, StreamRevision streamRevision) {
        super(eventId, eventType, payload, metadata);
        this.streamRevision = streamRevision;
    }

    public StreamRevision getStreamRevision() {
        return streamRevision;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("id", getEventId().getUuid().toString()) //
                .add("eventType", getEventType().getValue()) //
                .add("payload", getPayload()) //
                .add("metadata", getMetadata()) //
                .add("streamRevision", streamRevision).toString();
    }
}
