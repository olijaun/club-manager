package org.jaun.clubmanager.eventstore.akka;

import static java.util.Collections.singletonList;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jaun.clubmanager.domain.model.commons.EventId;
import org.jaun.clubmanager.eventstore.ConcurrencyException;
import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StoredEventData;
import org.jaun.clubmanager.eventstore.StoredEvents;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamNotFoundException;
import org.jaun.clubmanager.eventstore.StreamRevision;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.PatternsCS;

public class AkkaEventStore implements EventStoreClient {

    private final ActorSystem actorSystem;
    //private final ActorMaterializer actorMaterializer = ActorMaterializer.create(actorSystem);

    public AkkaEventStore(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

//    public static void main(String[] args) throws Exception {
//        AkkaEventStore store = new AkkaEventStore();
//
//        StreamId streamId = StreamId.parse("abc-1");
//
//        //EventData event1 = new EventData(EventId.generate(), new EventType("typeA"), "testdata1", "testmetadata1");
//        EventData event2 = new EventData(EventId.generate(), new EventType("typeB"), "testdata2", "testmetadata2");
//        EventData event3 = new EventData(EventId.generate(), new EventType("typeC"), "testdata3", "testmetadata3");
//
////        store.append(streamId, singletonList(event1), StreamRevision.NEW_STREAM);
////        store.append(streamId, singletonList(event2), StreamRevision.from(0));
////        store.append(streamId, singletonList(event3), StreamRevision.from(1));
//
//        store.append(streamId, singletonList(event2), StreamRevision.UNSPECIFIED);
//
//        StoredEvents storedEvents = store.read(streamId);
//        storedEvents.forEach(storedEvent -> System.out.println("event read : " + storedEvent));
//    }

    @Override
    public void append(StreamId streamId, List<EventData> evenDataList, StreamRevision expectedVersion)
            throws ConcurrencyException {

        ActorRef eventStream = actorSystem.actorOf(Props.create(EventStream.class, () -> new EventStream(streamId)));

        Object response;
        try {
            response = PatternsCS.ask(eventStream, new Append(evenDataList, expectedVersion), Duration.ofSeconds(5))
                    .toCompletableFuture()
                    .get();
        } catch (Exception e) {
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
            return new StoredEventData(streamId, e.getEventId(), e.getEventType(), e.getPayload(), e.getMetadata().orElse(null),
                    rev, Instant.now(), rev.getValue());
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
