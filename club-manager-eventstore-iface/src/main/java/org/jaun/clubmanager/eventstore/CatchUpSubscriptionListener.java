package org.jaun.clubmanager.eventstore;

import java.util.Optional;

public interface CatchUpSubscriptionListener {
    void onEvent(CatchUpSubscription subscription, StoredEventData eventData);

    void onClose(CatchUpSubscription subscription, Optional<Exception> e);
}
