package org.jaun.clubmanager.eventstore;

public interface CatchUpSubscriptionListener {
    void onEvent(CatchUpSubscription subscription, StoredEventData eventData);
}
