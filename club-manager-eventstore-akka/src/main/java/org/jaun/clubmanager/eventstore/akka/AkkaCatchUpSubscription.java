package org.jaun.clubmanager.eventstore.akka;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;

import org.jaun.clubmanager.domain.model.commons.EventMapping;
import org.jaun.clubmanager.eventstore.CatchUpSubscription;
import org.jaun.clubmanager.eventstore.CatchUpSubscriptionListener;
import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StoredEventData;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamRevision;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.persistence.jdbc.query.javadsl.JdbcReadJournal;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.Offset;
import akka.persistence.query.PersistenceQuery;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;

public class AkkaCatchUpSubscription implements CatchUpSubscription, CatchUpSubscriptionListener {

    private Map<EventType, BiConsumer<Long, StoredEventData>> map = new HashMap<>();
    private final List<String> categories;

    private final ActorSystem actorSystem = ActorSystem.create("test");
    private final ActorMaterializer actorMaterializer = ActorMaterializer.create(actorSystem);

    public AkkaCatchUpSubscription(String... categories) {
        this.categories = Arrays.asList(categories);
    }

    protected void registerMapping(EventMapping eventMapping, BiConsumer<Long, StoredEventData> event) {
        map.put(eventMapping.getEventType(), event);
    }

    public static void main(String[] args) {
        new AkkaCatchUpSubscription("bla").start();
    }

    @Override
    public void start() {
        JdbcReadJournal readJournal =
                PersistenceQuery.get(actorSystem).getReadJournalFor(JdbcReadJournal.class, JdbcReadJournal.Identifier());

        Source<EventEnvelope, NotUsed> source = readJournal.eventsByTag("category.abc", Offset.noOffset());

        CompletionStage<Done> stage = source.runForeach(envelope -> {

            EventData eventData = (EventData) envelope.event();

            StoredEventData storedEventData =
                    new StoredEventData(StreamId.parse(envelope.persistenceId()), eventData.getEventId(), eventData.getEventType(),
                            eventData.getPayload(), eventData.getMetadata().orElse(null),
                            StreamRevision.from(envelope.sequenceNr()), Instant.now(), envelope.sequenceNr());

            onEvent(this, storedEventData);

        }, actorMaterializer);

//        try {
//            // wait until all current events are loaded
//            stage.toCompletableFuture().get();
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }
        stage.exceptionally(e -> {
            throw new RuntimeException(e);
        });
    }

    @Override
    public void stop() {
        // TODO: stop subscription
        onClose(this, Optional.empty());
    }

    @Override
    public void onEvent(CatchUpSubscription subscription, StoredEventData storedEventData) {
        System.out.println("received event: " + storedEventData);
    }

    @Override
    public void onClose(CatchUpSubscription subscription, Optional<Exception> exception) {
        System.out.println("Subscription closed.");
        exception.ifPresent(Exception::printStackTrace);
    }
}
