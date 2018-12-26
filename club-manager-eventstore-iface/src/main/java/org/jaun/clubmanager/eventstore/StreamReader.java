package org.jaun.clubmanager.eventstore;

public interface StreamReader {
    void subscribe(CatchUpSubscriptionListener catchUpSubscriptionListener);
}
