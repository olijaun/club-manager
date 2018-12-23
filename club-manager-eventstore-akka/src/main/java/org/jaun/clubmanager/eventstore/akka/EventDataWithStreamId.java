package org.jaun.clubmanager.eventstore.akka;

import org.jaun.clubmanager.domain.model.commons.EventId;
import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StreamId;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class EventDataWithStreamId extends EventData {

    private final StreamId streamId;

    public EventDataWithStreamId(StreamId streamId, EventData eventData) {
        super(eventData);

        this.streamId = requireNonNull(streamId);
    }

    public EventDataWithStreamId(StreamId streamId, EventId eventId, EventType eventType, String payload, String metadata) {
        super(eventId, eventType, payload, metadata);

        this.streamId = streamId;
    }

    public StreamId getStreamId() {
        return streamId;
    }
}
