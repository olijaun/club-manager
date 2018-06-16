package org.jaun.clubmanager.eventstore.client.jaxrs;

import java.util.Timer;
import java.util.TimerTask;

import org.jaun.clubmanager.eventstore.CatchUpSubscription;
import org.jaun.clubmanager.eventstore.CatchUpSubscriptionListener;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.StoredEvents;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamRevision;

public class JaxRsCatchUpSubscription implements CatchUpSubscription {

    private final EventStoreClient eventStoreClient;
    private final Timer timer;
    private final CatchUpSubscriptionListener catchUpSubscriptionListener;
    private final StreamId streamId;
    private StreamRevision currentRevision;
    private final JaxRsCatchUpSubscription me = this;

    public JaxRsCatchUpSubscription(EventStoreClient eventStoreClient, StreamId streamId, StreamRevision fromRevision,
            CatchUpSubscriptionListener subscriptionListener) {

        this.streamId = streamId;
        this.eventStoreClient = eventStoreClient;
        this.catchUpSubscriptionListener = subscriptionListener;
        this.timer = new Timer();
        this.currentRevision = fromRevision;
    }

    public void start() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println("checking for events");
                StoredEvents storedEvents = eventStoreClient.read(streamId, currentRevision, StreamRevision.MAXIMUM);

                storedEvents.stream().forEach(e -> catchUpSubscriptionListener.onEvent(me, e));

                if (storedEvents.isEmpty()) {
                    return;
                } else {
                    currentRevision = storedEvents.highestRevision().get();
                }
            }
        }, 0, 5 * 1000);
    }

    public void stop() {
        timer.cancel();
    }
}
