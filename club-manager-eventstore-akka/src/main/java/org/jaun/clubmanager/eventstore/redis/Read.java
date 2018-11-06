package org.jaun.clubmanager.eventstore.redis;

import java.io.Serializable;

import org.jaun.clubmanager.eventstore.StreamRevision;

public class Read implements Serializable {
    private final StreamRevision from;
    private final StreamRevision to;

    public Read(StreamRevision from, StreamRevision to) {
        this.from = from;
        this.to = to;
    }

    public StreamRevision getFromVersion() {
        return from;
    }

    public StreamRevision getToVersion() {
        return to;
    }
}
