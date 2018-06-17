package org.jaun.clubmanager.eventstore;

import org.jaun.clubmanager.eventstore.StreamId;

public class StreamNotFoundException extends Exception {
    public StreamNotFoundException(StreamId streamId) {
        super("stream not found: " + streamId);
    }
}
