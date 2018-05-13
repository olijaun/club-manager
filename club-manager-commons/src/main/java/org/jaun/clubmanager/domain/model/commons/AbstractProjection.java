package org.jaun.clubmanager.domain.model.commons;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.github.msemys.esjc.CatchUpSubscription;
import com.github.msemys.esjc.CatchUpSubscriptionListener;
import com.github.msemys.esjc.CatchUpSubscriptionSettings;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.SubscriptionDropReason;
import com.google.gson.Gson;

public abstract class AbstractProjection {

    private Map<String, Consumer<ResolvedEvent>> map = new HashMap<>();
    private final List<String> streams;
    private final Gson gson = new Gson();

    private final EventStore eventStore;

    public AbstractProjection(EventStore eventStore, String... streams) {
        this.eventStore = eventStore;
        this.streams = Arrays.asList(streams);
    }

    protected void registerMapping(EventMapping eventMapping, Consumer<ResolvedEvent> event) {
        map.put(eventMapping.getEventType(), event);
    }

    protected void registerMapping(String eventType, Consumer<ResolvedEvent> event) {
        map.put(eventType, event);
    }

    private class MyCatchUpSubscriptionListener implements CatchUpSubscriptionListener {

        public void onLiveProcessingStarted(CatchUpSubscription subscription) {
            System.out.println("Live processing started!");
        }

        public void onEvent(CatchUpSubscription subscription, ResolvedEvent event) {
            try {
                if (map.containsKey(event.event.eventType)) {
                    map.get(event.event.eventType).accept(event);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        public void onClose(CatchUpSubscription subscription, SubscriptionDropReason reason, Exception exception) {
            System.out.println("Subscription closed: " + reason);
            exception.printStackTrace();
        }
    }

    public void startSubscription() {

        // TODO: make connection stuff configurable
//        EventStore eventStore =
//                EventStoreBuilder.newBuilder().singleNodeAddress("127.0.0.1", 1113).userCredentials("admin", "changeit").build();

        CatchUpSubscriptionSettings settings = CatchUpSubscriptionSettings.newBuilder().resolveLinkTos(true).build();

        // TODO: close connection/subscription on shutdown

        for (String streamName : streams) {

            CatchUpSubscription catchupSubscription =
                    eventStore.subscribeToStreamFrom(streamName, null, settings, new MyCatchUpSubscriptionListener());

        }

        //eventStore.subscribeToAll()
    }


    protected <T> T toObject(ResolvedEvent resolvedEvent, Class<T> domainEventClass) {

        try {
            return gson.fromJson(new String(resolvedEvent.event.data, "UTF-8"), domainEventClass);
            // getEventClass(resolvedEvent.event.eventType));
        } catch (RuntimeException | UnsupportedEncodingException e) {
            throw new IllegalStateException("could not deserialize event string to object: " + resolvedEvent.event.eventType, e);
        }
    }
}
