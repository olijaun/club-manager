package org.jaun.clubmanager.eventstore;

public interface CatchUpSubscription {
    void start();

    void stop();
}
