package org.jaun.clubmanager.eventstore;

import java.util.List;

public interface EventStore {

    StreamRevision append(StreamId streamId, EventData evenData, StreamRevision expectedVersion) throws ConcurrencyException;

    List<StoredEventData> read(StreamId streamId);

    List<StoredEventData> read(StreamId streamId, StreamRevision versionGreaterThan);
}
