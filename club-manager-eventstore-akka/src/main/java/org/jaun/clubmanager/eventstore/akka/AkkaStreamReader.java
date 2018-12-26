package org.jaun.clubmanager.eventstore.akka;

import akka.persistence.query.javadsl.EventsByTagQuery;
import akka.stream.ActorMaterializer;
import com.google.gson.Gson;
import org.jaun.clubmanager.eventstore.*;

import java.util.*;

public class AkkaStreamReader implements StreamReader {

    private final Gson gson = new Gson();
    private final EventsByTagQuery readJournal;
    private final ActorMaterializer actorMaterializer;
    private final List<AkkaCatchUpSubscription> akkaCatchUpSubscriptions;

    public AkkaStreamReader(ActorMaterializer actorMaterializer, EventsByTagQuery readJournal) {
        this.actorMaterializer = actorMaterializer;
        this.readJournal = readJournal;
        this.akkaCatchUpSubscriptions = new ArrayList<>();
    }

    @Override
    public void subscribe(CatchUpSubscriptionListener catchUpSubscriptionListener) {
        AkkaCatchUpSubscription subscription = new AkkaCatchUpSubscription(actorMaterializer, readJournal, catchUpSubscriptionListener, catchUpSubscriptionListener.categories());

        akkaCatchUpSubscriptions.add(subscription);
        subscription.start();
    }
}
