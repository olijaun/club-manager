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

import com.google.gson.Gson;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.persistence.jdbc.query.javadsl.JdbcReadJournal;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.Offset;
import akka.persistence.query.PersistenceQuery;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;

public abstract class AbstractAkkaCatchUpSubscription implements CatchUpSubscription, CatchUpSubscriptionListener {

    private final ActorSystem actorSystem;
    private Map<EventType, BiConsumer<Long, StoredEventData>> map = new HashMap<>();
    private final List<String> categories;
    private final Gson gson = new Gson();

    private final ActorMaterializer actorMaterializer;

    public AbstractAkkaCatchUpSubscription(ActorSystem actorSystem, String... categories) {
        this.categories = Arrays.asList(categories);
        this.actorSystem = actorSystem;
        this.actorMaterializer = ActorMaterializer.create(actorSystem);
    }

    protected void registerMapping(EventMapping eventMapping, BiConsumer<Long, StoredEventData> event) {
        map.put(eventMapping.getEventType(), event);
    }

    protected void registerMapping(EventType eventType, BiConsumer<Long, StoredEventData> event) {
        map.put(eventType, event);
    }

    @Override
    public void start() {
        JdbcReadJournal readJournal =
                PersistenceQuery.get(actorSystem).getReadJournalFor(JdbcReadJournal.class, JdbcReadJournal.Identifier());

        for (String category : categories) {

            Source<EventEnvelope, NotUsed> source = readJournal.eventsByTag("category." + category, Offset.noOffset());

            CompletionStage<Done> stage = source.runForeach(envelope -> {

                EventData eventData = (EventData) envelope.event();

                StoredEventData storedEventData =
                        new StoredEventData(StreamId.parse(envelope.persistenceId()), eventData.getEventId(),
                                eventData.getEventType(), eventData.getPayload(), eventData.getMetadata().orElse(null),
                                StreamRevision.from(envelope.sequenceNr()), Instant.now(), envelope.sequenceNr());

                onEvent(this, storedEventData);

            }, actorMaterializer);

            stage.exceptionally(e -> {
                throw new RuntimeException(e);
            });
        }

//        try {
//            // wait until all current events are loaded
//            stage.toCompletableFuture().get();
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }

    }

    @Override
    public void stop() {
        // TODO: stop subscription
        onClose(this, Optional.empty());
    }

    public void onEvent(CatchUpSubscription subscription, StoredEventData event) {
        try {
            if (map.containsKey(event.getEventType())) {
                map.get(event.getEventType()).accept(event.getStreamRevision().getValue(), event);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(CatchUpSubscription subscription, Optional<Exception> exception) {
        System.out.println("Subscription closed.");
        exception.ifPresent(Exception::printStackTrace);
    }

    protected <T> T toObject(StoredEventData resolvedEvent, Class<T> domainEventClass) {

        try {
            return gson.fromJson(resolvedEvent.getPayload(), domainEventClass);
            // getEventClass(resolvedEvent.event.eventType));
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "could not deserialize event string to object: " + resolvedEvent.getEventType().getValue(), e);
        }
    }

}
