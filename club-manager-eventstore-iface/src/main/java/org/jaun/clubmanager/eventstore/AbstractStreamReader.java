package org.jaun.clubmanager.eventstore;

import java.util.function.BiConsumer;

public interface AbstractStreamReader {
    void subscribeFrom(EventType eventType, BiConsumer<Long, StoredEventData> consumer);
}
