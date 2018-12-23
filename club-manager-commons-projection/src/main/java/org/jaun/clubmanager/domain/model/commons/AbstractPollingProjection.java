package org.jaun.clubmanager.domain.model.commons;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.jaun.clubmanager.eventstore.CatchUpSubscription;
import org.jaun.clubmanager.eventstore.CatchUpSubscriptionListener;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StoredEventData;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamRevision;

import com.google.gson.Gson;

public abstract class AbstractPollingProjection {

    private Map<EventType, BiConsumer<Long, StoredEventData>> map = new HashMap<>();
    private final List<String> streams;
    private final Gson gson = new Gson();

    private final Map<String, CatchUpSubscription> subscriptionMap = new ConcurrentHashMap<>();

    // TODO: make private
    protected final EventStoreClient eventStoreClient;

    public AbstractPollingProjection(EventStoreClient eventStoreClient, String... streams) {
        this.eventStoreClient = eventStoreClient;
        this.streams = Arrays.asList(streams);
    }

    protected void registerMapping(EventMapping eventMapping, BiConsumer<Long, StoredEventData> event) {
        map.put(eventMapping.getEventType(), event);
    }

    protected void registerMapping(EventType eventType, BiConsumer<Long, StoredEventData> event) {
        map.put(eventType, event);
    }

    private class MyCatchUpSubscriptionListener implements CatchUpSubscriptionListener {

        @Override
        public void onEvent(CatchUpSubscription subscription, StoredEventData event) {
            try {
                if (map.containsKey(event.getEventType())) {
                    map.get(event.getEventType()).accept(event.getStreamRevision().getValue(), event);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onClose(CatchUpSubscription subscription, Optional<Exception> exception) {
            System.out.println("Subscription closed.");
            exception.ifPresent(Exception::printStackTrace);
        }
    }

    public void startSubscriptions() {

        for (String streamName : streams) {
            startSubscription(streamName, StreamRevision.INITIAL);
        }
    }

    /**
     * @param streamName
     * @param fromVersion
     *         NULL is from start... oh dear
     */
    public abstract void startSubscription(String streamName, StreamRevision fromVersion);

    public abstract void stopSubscription(String streamName);

    public void stopSubscriptions() {

        for (CatchUpSubscription subscription : subscriptionMap.values()) {

            try {
                subscription.stop(); //Duration.of(5, ChronoUnit.SECONDS));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        subscriptionMap.clear();
    }

    protected <T> T toObject(StoredEventData resolvedEvent, Class<T> domainEventClass) {

        try {
            return gson.fromJson(resolvedEvent.getPayload(), domainEventClass);
            // getEventClass(resolvedEvent.event.eventType));
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "could not deserialize event string to object: " + resolvedEvent.getEventType().getValue(), e);
        }
    }
}
