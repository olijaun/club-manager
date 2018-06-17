package org.jaun.clubmanager.eventstore;

import java.util.List;

public interface EventStoreClient {

    /**
     * @param streamId
     * @param evenDataList
     * @param expectedVersion
     * @throws ConcurrencyException
     */
    void append(StreamId streamId, List<EventData> evenDataList, StreamRevision expectedVersion) throws ConcurrencyException;

    StoredEvents read(StreamId streamId) throws StreamNotFoundException;

    StoredEvents read(StreamId streamId, StreamRevision fromRevision) throws StreamNotFoundException;

    StoredEvents read(StreamId streamId, StreamRevision fromRevision, StreamRevision toRevision) throws StreamNotFoundException;
}
