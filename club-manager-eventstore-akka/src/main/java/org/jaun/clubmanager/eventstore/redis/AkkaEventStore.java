package org.jaun.clubmanager.eventstore.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import org.jaun.clubmanager.eventstore.ConcurrencyException;
import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.EventStore;
import org.jaun.clubmanager.eventstore.StoredEventData;
import org.jaun.clubmanager.eventstore.StoredEvents;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamNotFoundException;
import org.jaun.clubmanager.eventstore.StreamRevision;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import akka.persistence.jdbc.query.javadsl.JdbcReadJournal;
import akka.persistence.query.EventEnvelope;
import akka.persistence.query.Offset;
import akka.persistence.query.PersistenceQuery;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;

public class AkkaEventStore implements EventStore {

    private final ActorSystem actorSystem = ActorSystem.create("test");
    private final ActorMaterializer actorMaterializer = ActorMaterializer.create(actorSystem);

    public AkkaEventStore() {
    }

    public static void main(String[] args) throws Exception {
        AkkaEventStore store = new AkkaEventStore();

        String streamId = "test-1";

        Event event1 = new Event(UUID.randomUUID().toString(), "testdata1", "testmetadata1");
        Event event2 = new Event(UUID.randomUUID().toString(), "testdata2", "testmetadata2");
        Event event3 = new Event(UUID.randomUUID().toString(), "testdata3", "testmetadata3");

        store.appendEvent(streamId, Collections.singletonList(event1), 0);
        store.appendEvent(streamId, Collections.singletonList(event2), 1);
        int currentStreamVersion = store.appendEvent(streamId, Collections.singletonList(event3), 2);
//
//        System.out.println("currentStreamVersion: " + currentStreamVersion);
        List<Event> events = store.readEvents(streamId, 0, 2);
        events.forEach(eventRead -> System.out.println("event read : " + eventRead.getData()));
    }

    @Override
    public long length(StreamId streamId) {
        ActorRef eventStream = actorSystem.actorOf(Props.create(EventStream.class, () -> new EventStream(streamId)));

        try {
            return ((Integer) PatternsCS.ask(eventStream, new QueryLength(), Duration.ofSeconds(5))
                    .toCompletableFuture()
                    .get()).longValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StreamRevision append(StreamId streamId, List<EventData> evenDataList, StreamRevision expectedVersion)
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

        return StreamRevision.from((Integer) response);
    }

    @Override
    public StoredEvents read(StreamId streamId, StreamRevision fromRevision, StreamRevision toRevision)
            throws StreamNotFoundException {

        if (streamId.getValue().startsWith("$ce-")) {
            read(streamId, fromRevision)
        }

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

        List<EventData> events = (List<Event>) response;

        if (events.isEmpty()) {
            throw new StreamNotFoundException(streamId);
        }

        return events;
    }

    public List<StoredEventData> readEventsUsingReadJournal(StreamId streamId) {
        JdbcReadJournal readJournal =
                PersistenceQuery.get(actorSystem).getReadJournalFor(JdbcReadJournal.class, JdbcReadJournal.Identifier());

        Source<EventEnvelope, NotUsed> source = readJournal.currentEventsByPersistenceId(streamId.getValue(), 0, Long.MAX_VALUE);

        readJournal.currentEventsByTag("category_" + streamId.getCategory().get().getName(), Offset.noOffset());
                ArrayList < EventData > streamEvents = new ArrayList<>();

        CompletionStage<Done> stage = source.runForeach(envelope -> {
            streamEvents.add((EventData) envelope.event());
        }, actorMaterializer);

        try {
            // wait until all current events are loaded
            stage.toCompletableFuture().get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        stage.exceptionally(e -> {
            throw new RuntimeException(e);
        });

        streamEvents.stream().map()

        return streamEvents;
    }
}
