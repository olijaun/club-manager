package org.jaun.clubmanager.eventstore;

public interface StreamReader {
    void subscribeFrom(int offset, CatchUpSubscriptionListener catchUpSubscriptionListener);
}
