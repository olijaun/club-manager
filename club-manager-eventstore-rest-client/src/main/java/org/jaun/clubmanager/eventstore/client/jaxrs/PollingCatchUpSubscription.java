package org.jaun.clubmanager.eventstore.client.jaxrs;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.jaun.clubmanager.eventstore.CatchUpSubscription;
import org.jaun.clubmanager.eventstore.CatchUpSubscriptionListener;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.StoredEvents;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamNotFoundException;
import org.jaun.clubmanager.eventstore.StreamRevision;

public class PollingCatchUpSubscription implements CatchUpSubscription {

    private final EventStoreClient eventStoreClient;
    private final Timer timer;
    private final CatchUpSubscriptionListener catchUpSubscriptionListener;
    private final StreamId streamId;
    private StreamRevision currentRevision;
    private final PollingCatchUpSubscription me = this;
    private final int periodInMilliSeconds;

    public PollingCatchUpSubscription(EventStoreClient eventStoreClient, StreamId streamId, StreamRevision fromRevision,
            int periodInMilliSeconds, CatchUpSubscriptionListener subscriptionListener) {

        this.streamId = streamId;
        this.eventStoreClient = eventStoreClient;
        this.catchUpSubscriptionListener = subscriptionListener;
        this.timer = new Timer();
        this.currentRevision = fromRevision;
        this.periodInMilliSeconds = periodInMilliSeconds;
    }

    public void start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println(streamId.getValue() + ": checking for events");
                StoredEvents storedEvents = null;
                try {
                    storedEvents = eventStoreClient.read(streamId, currentRevision, StreamRevision.MAXIMUM);
                } catch (StreamNotFoundException e) {
                    System.out.println("stream not found: " + streamId.getValue());
                    // do noting as long as the stream does not exist
                    return;
                } catch(Exception e) {
                    e.printStackTrace();
                    return;
                }

                storedEvents.stream().forEach(e -> catchUpSubscriptionListener.onEvent(me, e));

                if (storedEvents.isEmpty()) {
                    return;
                } else {
                    currentRevision = storedEvents.highestPosition().get().add(1);
                }
            }
        }, 0, periodInMilliSeconds);
    }

    public void stop() {
        timer.cancel();
        catchUpSubscriptionListener.onClose(this, Optional.empty());
    }
}
