package org.jaun.clubmanager.eventstore;

import java.time.Instant;

import org.jaun.clubmanager.domain.model.commons.EventId;

import com.google.common.base.MoreObjects;

/**
 * Event data. Containing the deserialized event.
 */
public class StoredEventData extends EventData {

    private final StreamRevision streamRevision;
    private final Instant timestamp;
    private final StreamId streamId;
    private final long position;

    /**
     * @param eventId
     * @param payload
     *         The actual event serialized into a string (could be a JSON).
     * @param metadata
     */
    public StoredEventData(StreamId streamId, EventId eventId, EventType eventType, String payload, String metadata,
            StreamRevision streamRevision, Instant timestamp, long position) {

        super(eventId, eventType, payload, metadata);
        this.streamRevision = streamRevision;
        this.timestamp = timestamp;
        this.streamId = streamId;
        this.position = position;
    }

    public StreamRevision getStreamRevision() {
        return streamRevision;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public StreamId getStreamId() {
        return streamId;
    }

    public long getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this) //
                .add("id", getEventId().getUuid().toString()) //
                .add("streamId", streamId.getValue())
                .add("timestamp", timestamp.getEpochSecond())
                .add("eventType", getEventType().getValue()) //
                .add("payload", getPayload()) //
                .add("metadata", getMetadata()) //
                .add("streamRevision", streamRevision.getValue()) //
                .add("position", position) //
                .toString();
    }
}
