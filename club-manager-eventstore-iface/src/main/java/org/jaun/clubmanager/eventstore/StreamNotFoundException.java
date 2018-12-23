package org.jaun.clubmanager.eventstore;

import org.jaun.clubmanager.eventstore.StreamId;

public class StreamNotFoundException extends Exception {
    private final StreamId streamId;

    public StreamNotFoundException(StreamId streamId) {
        super("stream not found: " + streamId);
        this.streamId = streamId;
    }

    public StreamId getStreamId() {
        return streamId;
    }
}
