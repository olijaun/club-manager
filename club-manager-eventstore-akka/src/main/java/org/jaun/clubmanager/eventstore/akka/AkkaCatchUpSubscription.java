package org.jaun.clubmanager.eventstore.akka;

import akka.Done;
import akka.NotUsed;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.Offset;
import akka.persistence.query.javadsl.EventsByTagQuery;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.RestartSource;
import akka.stream.javadsl.Source;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import org.jaun.clubmanager.eventstore.*;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import static java.util.Objects.requireNonNull;

class AkkaCatchUpSubscription {

    private final Gson gson = new Gson();
    private final List<Category> categories;
    private final EventsByTagQuery readJournal;
    private final ActorMaterializer actorMaterializer;
    private final CatchUpSubscriptionListener listener;

    AkkaCatchUpSubscription(ActorMaterializer actorMaterializer, EventsByTagQuery readJournal, CatchUpSubscriptionListener listener, Collection<Category> categories) {
        this.categories = ImmutableList.copyOf(categories);
        this.actorMaterializer = requireNonNull(actorMaterializer);
        this.readJournal = requireNonNull(readJournal);
        this.listener = requireNonNull(listener);
    }

    public void start() {

        for (Category category : categories) {
            System.out.println("eventByTag by category: " + category.getName());

            Source<EventEnvelope, NotUsed> sourceWithBackoff =
                    RestartSource.withBackoff(Duration.of(3, ChronoUnit.SECONDS), // min backoff
                            Duration.of(30, ChronoUnit.SECONDS), // max backoff
                            0.2, // adds 20% "noise" to vary the intervals slightly
                            () -> {
                                System.out.println("restart.......................");
                                return readJournal.eventsByTag("category." + category.getName(), Offset.noOffset());
                            });

            // TODO: https://doc.akka.io/docs/akka/2.5.5/java/stream/stream-error.html
            CompletionStage<Done> stage = sourceWithBackoff.runForeach(envelope -> {

                EventData eventData = (EventData) envelope.event();

                StoredEventData storedEventData = new StoredEventData.Builder()
                        .streamId(StreamId.parse(envelope.persistenceId()))
                        .eventId(eventData.getEventId())
                        .eventType(eventData.getEventType())
                        .payload(eventData.getPayload())
                        .metadata(eventData.getMetadata().orElse(null))
                        .streamRevision(StreamRevision.from(envelope.sequenceNr()))
                        .timestamp(Instant.now())
                        .position(envelope.sequenceNr()).build();

                listener.onEvent(storedEventData);

            }, actorMaterializer);

            stage.exceptionally(e -> {
                e.printStackTrace();
                throw new RuntimeException(e);
            });
        }
    }

    public void stop() {
        // TODO: stop subscription
        onClose(Optional.empty());
    }

    public void onClose(Optional<Exception> exception) {
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
