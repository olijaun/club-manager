package org.jaun.clubmanager.domain.model.commons;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.jaun.clubmanager.eventstore.CatchUpSubscriptionListener;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.StoredEventData;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamRevision;
import org.jaun.clubmanager.eventstore.client.jaxrs.JaxRsCatchUpSubscription;

import com.github.msemys.esjc.CatchUpSubscription;
import com.github.msemys.esjc.CatchUpSubscriptionSettings;
import com.github.msemys.esjc.SubscriptionDropReason;
import com.google.gson.Gson;

public abstract class AbstractPollingProjection {

    private Map<String, BiConsumer<Long, StoredEventData>> map = new HashMap<>();
    private final List<String> streams;
    private final Gson gson = new Gson();

    private final Map<String, org.jaun.clubmanager.eventstore.CatchUpSubscription> subscriptionMap = new ConcurrentHashMap<>();

    // TODO: make private
    protected final EventStoreClient eventStoreClient;

    public AbstractPollingProjection(EventStoreClient eventStoreClient, String... streams) {
        this.eventStoreClient = eventStoreClient;
        this.streams = Arrays.asList(streams);
    }

    protected void registerMapping(EventMapping eventMapping, BiConsumer<Long, StoredEventData> event) {
        map.put(eventMapping.getEventType(), event);
    }

    protected void registerMapping(String eventType, BiConsumer<Long, StoredEventData> event) {
        map.put(eventType, event);
    }

    private class MyCatchUpSubscriptionListener implements CatchUpSubscriptionListener {

        @Override
        public void onEvent(org.jaun.clubmanager.eventstore.CatchUpSubscription subscription, StoredEventData event) {
            try {
                if (map.containsKey(event.getEventType().getValue())) {
                    map.get(event.getEventType()).accept(event.getStreamRevision().getValue(), event);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        public void onClose(CatchUpSubscription subscription, SubscriptionDropReason reason, Exception exception) {
            System.out.println("Subscription closed: " + reason);
            if (exception != null) {
                exception.printStackTrace();
            }
        }
    }

    public void startSubscriptions() {

        for (String streamName : streams) {
            startSubscription(streamName, null);
        }
    }

    /**
     * @param streamName
     * @param fromVersion
     *         NULL is from start... oh dear
     */
    public void startSubscription(String streamName, Long fromVersion) {

        CatchUpSubscriptionSettings settings = CatchUpSubscriptionSettings.newBuilder().resolveLinkTos(true).build();

        org.jaun.clubmanager.eventstore.CatchUpSubscription catchupSubscription =
                new JaxRsCatchUpSubscription(eventStoreClient, StreamId.parse(streamName), StreamRevision.from(fromVersion),
                        new MyCatchUpSubscriptionListener());

        subscriptionMap.put(streamName, catchupSubscription);
    }

    public void stopSubscription(String streamName) {

        try {
            subscriptionMap.get(streamName).stop();
            subscriptionMap.remove(streamName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSubscriptions() {

        for (org.jaun.clubmanager.eventstore.CatchUpSubscription subscription : subscriptionMap.values()) {

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
