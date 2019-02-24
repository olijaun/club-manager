package org.jaun.clubmanager.eventstore.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import org.jaun.clubmanager.eventstore.*;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AkkaEventStore implements EventStoreClient {

    private final ActorSystem actorSystem;

    public AkkaEventStore(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    @Override
    public void append(StreamId streamId, List<EventData> evenDataList, StreamRevision expectedVersion)
            throws ConcurrencyException {

        ActorRef eventStream = actorSystem.actorOf(Props.create(EventStream.class, () -> new EventStream(streamId)));

        Object response;
        try {
            response = PatternsCS.ask(eventStream, new Append(evenDataList, expectedVersion), Duration.ofSeconds(5))
                    .toCompletableFuture()
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        if (response instanceof ConcurrencyException) {
            throw (ConcurrencyException) response;
        } else if (response instanceof Exception) {
            throw new RuntimeException((Exception) response);
        }
    }

    @Override
    public StoredEvents read(StreamId streamId, StreamRevision fromRevision, StreamRevision toRevision)
            throws StreamNotFoundException {

        ActorRef eventStream = actorSystem.actorOf(Props.create(EventStream.class, () -> new EventStream(streamId)));

        Object response;
        try {
            response = PatternsCS.ask(eventStream, new Read(fromRevision, toRevision), Duration.ofSeconds(5))
                    .toCompletableFuture()
                    .get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (response instanceof Exception) {
            throw new RuntimeException((Exception) response);
        }

        List<EventData> events = (List<EventData>) response;

        if (events.isEmpty()) {
            throw new StreamNotFoundException(streamId);
        }

        AtomicInteger revisionCounter = new AtomicInteger(fromRevision.getValue().intValue());
        List<StoredEventData> storedEvents = events.stream().map(e -> {

            StreamRevision rev = StreamRevision.from(revisionCounter.getAndIncrement());

            return new StoredEventData.Builder()
                    .streamId(streamId)
                    .eventId(e.getEventId())
                    .eventType(e.getEventType())
                    .payload(e.getPayload())
                    .metadata(e.getMetadata().orElse(null))
                    .streamRevision(rev)
                    .timestamp(Instant.now())
                    .position(rev.getValue()).build();

        }).collect(Collectors.toList());

        return new StoredEvents(storedEvents);
    }

    @Override
    public StoredEvents read(StreamId streamId) throws StreamNotFoundException {
        return read(streamId, StreamRevision.INITIAL, StreamRevision.MAXIMUM);
    }

    @Override
    public StoredEvents read(StreamId streamId, StreamRevision fromRevision) throws StreamNotFoundException {
        return read(streamId, fromRevision, StreamRevision.MAXIMUM);
    }

//    public List<StoredEventData> readEventsUsingReadJournal(StreamId streamId) {
//        JdbcReadJournal readJournal =
//                PersistenceQuery.get(actorSystem).getReadJournalFor(JdbcReadJournal.class, JdbcReadJournal.Identifier());
//
//        Source<EventEnvelope, NotUsed> source = readJournal.currentEventsByPersistenceId(streamId.getValue(), 0, Long.MAX_VALUE);
//
//        readJournal.currentEventsByTag("category_" + streamId.getCategory().get().getName(), Offset.noOffset());
//        ArrayList<EventData> streamEvents = new ArrayList<>();
//
//        CompletionStage<Done> stage = source.runForeach(envelope -> {
//            streamEvents.add((EventData) envelope.event());
//        }, actorMaterializer);
//
//        try {
//            // wait until all current events are loaded
//            stage.toCompletableFuture().get();
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }
//        stage.exceptionally(e -> {
//            throw new RuntimeException(e);
//        });
//
//        streamEvents.stream().map()
//
//        return streamEvents;
//    }
}
