package org.jaun.clubmanager.eventstore;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public abstract class AbstractMappingCatchUpSubscriptionListener implements CatchUpSubscriptionListener {

    private Map<EventType, BiConsumer<Long, StoredEventData>> map = new HashMap<>();
    private final Gson gson = new Gson();

    public AbstractMappingCatchUpSubscriptionListener() {
    }

    public void registerMapping(EventType eventType, BiConsumer<Long, StoredEventData> event) {
        map.put(eventType, event);
    }

    @Override
    public void onEvent(StoredEventData event) {
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
