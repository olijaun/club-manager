package org.jaun.clubmanager.eventstore;

import com.google.common.base.MoreObjects;
import org.jaun.clubmanager.domain.model.commons.EventId;

import java.time.Instant;

/**
 * Stored Event data. Containing the deserialized event and information about the stream it is part of.
 */
public class StoredEventData extends EventData {

    private final StreamRevision streamRevision;
    private final Instant timestamp;
    private final StreamId streamId;
    private final long position;

    private StoredEventData(StoredEventData.Builder builder) {

        super(builder.eventId, builder.eventType, builder.payload, builder.metadata);
        this.streamRevision = builder.streamRevision;
        this.timestamp = builder.timestamp;
        this.streamId = builder.streamId;
        this.position = builder.position;
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

    public static class Builder {

        private EventId eventId;
        private EventType eventType;
        private String payload;
        private String metadata;
        private StreamRevision streamRevision;
        private Instant timestamp;
        private StreamId streamId;
        private long position;

        public StoredEventData build() {
            return new StoredEventData(this);
        }

        public Builder streamRevision(StreamRevision streamRevision) {
            this.streamRevision = streamRevision;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder streamId(StreamId streamId) {
            this.streamId = streamId;
            return this;
        }

        public Builder position(long position) {
            this.position = position;
            return this;
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
