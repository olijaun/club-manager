package org.jaun.clubmanager.evenstore;

import java.util.List;

public interface EventStore {

    void append(StreamId streamId, EventData evenData, StreamRevision expectedVersion) throws ConcurrencyException;

    List<StoredEventData> read(StreamId streamId);

    List<StoredEventData> read(StreamId streamId, StreamRevision versionGreaterThan);
}
