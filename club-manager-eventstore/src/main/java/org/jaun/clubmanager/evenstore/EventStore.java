package org.jaun.clubmanager.evenstore;

import java.util.List;

public interface EventStore {

    void append(StreamId streamId, EventData evenData, StreamRevision expectedVersion) throws ConcurrencyException;

    List<EventData> read(StreamId streamId);

    List<EventData> read(StreamId streamId, Integer versionGreaterThan);
}
