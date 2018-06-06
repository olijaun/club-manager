package org.jaun.clubmanager.eventstore;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class StoredEvents implements Iterable<StoredEventData> {

    private final List<StoredEventData> eventDataList;

    public StoredEvents(List<StoredEventData> eventDataList) {
        this.eventDataList = ImmutableList.copyOf(eventDataList);
    }

    public StreamRevision lowestRevision() {
        return eventDataList.get(0).getStreamRevision();
    }

    public StreamRevision highestRevision() {
        return eventDataList.get(eventDataList.size()-1).getStreamRevision();
    }

    public Stream<StoredEventData> stream() {
        return eventDataList.stream();
    }

    @Override
    public Iterator<StoredEventData> iterator() {
        return eventDataList.iterator();
    }

    public List<StoredEventData> newestFirstList() {
        return Lists.reverse(eventDataList);
    }

    public int size() {
        return eventDataList.size();
    }

    public boolean isEmpty() {
        return eventDataList.isEmpty();
    }
}
