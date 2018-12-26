package org.jaun.clubmanager.eventstore;

import java.util.function.BiConsumer;

public interface CatchUpSubscription {
    void start();
    void stop();


}
