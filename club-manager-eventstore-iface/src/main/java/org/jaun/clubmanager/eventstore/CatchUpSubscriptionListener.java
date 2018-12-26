package org.jaun.clubmanager.eventstore;

import java.util.Collection;
import java.util.Optional;

public interface CatchUpSubscriptionListener {
    void onEvent(StoredEventData eventData);
    void onClose(CatchUpSubscription subscription, Optional<Exception> e);

    Collection<Category> categories();
}
