package org.jaun.clubmanager.eventstore;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class StoredEvents implements Iterable<StoredEventData> {

    private final List<StoredEventData> eventDataList;

    public StoredEvents(List<StoredEventData> eventDataList) {
        try {
            this.eventDataList = ImmutableList.copyOf(eventDataList);
        } catch(NullPointerException e) {
            System.out.println("hello world");
            throw e;
        }
    }

    public Optional<StreamRevision> lowestPosition() {
        if (eventDataList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(StreamRevision.from(eventDataList.get(0).getPosition()));
    }

    public Optional<StreamRevision> highestPosition() {
        if (eventDataList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(StreamRevision.from(eventDataList.get(eventDataList.size() - 1).getPosition()));
    }

    public Stream<StoredEventData> stream() {
        return eventDataList.stream();
    }

    public Stream<StoredEventData> reverseStream() {
        return Lists.reverse(eventDataList).stream();
    }

    @Override
    public Iterator<StoredEventData> iterator() {
        return eventDataList.iterator();
    }

    public Optional<StoredEventData> highestPositionEvent() {
        if (eventDataList.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(eventDataList.get(eventDataList.size() - 1));
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
