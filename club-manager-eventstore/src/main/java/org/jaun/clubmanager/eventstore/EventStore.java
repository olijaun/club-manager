package org.jaun.clubmanager.eventstore;

import java.util.List;

public interface EventStore {

    /**
     * @param streamId
     * @param evenData
     * @param expectedVersion
     * @return Returns the revision of the first event in the list.
     * @throws ConcurrencyException
     */
    StreamRevision append(StreamId streamId, List<EventData> evenData, StreamRevision expectedVersion) throws ConcurrencyException;

    StoredEvents read(StreamId streamId) throws StreamNotFoundException;

    StoredEvents read(StreamId streamId, StreamRevision versionGreaterThan) throws StreamNotFoundException;

    StoredEvents read(StreamId streamId, StreamRevision fromRevision, StreamRevision toRevision) throws StreamNotFoundException;

    long length(StreamId streamId);
}
