package org.jaun.clubmanager.evenstore;

import java.util.List;

public interface EventStore {

    void append(StreamId streamId, List<EventData> eventList, Integer expectedVersion) throws ConcurrencyException;

    List<EventData> read(StreamId streamId);

    List<EventData> read(StreamId streamId, Integer versionGreaterThan);
}
