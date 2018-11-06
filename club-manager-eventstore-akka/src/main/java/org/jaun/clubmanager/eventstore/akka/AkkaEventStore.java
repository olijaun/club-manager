package org.jaun.clubmanager.eventstore.akka;

import java.time.Duration;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.PatternsCS;
import akka.stream.ActorMaterializer;

public class AkkaEventStore {

    private final ActorSystem actorSystem = ActorSystem.create("test");
    private final ActorMaterializer actorMaterializer = ActorMaterializer.create(actorSystem);

    public AkkaEventStore() {
    }

    public static void main(String[] args) throws Exception {
        AkkaEventStore store = new AkkaEventStore();

        String streamId = "test-1";

//        Event event1 = new Event(UUID.randomUUID().toString(), "testdata1", "testmetadata1");
//        Event event2 = new Event(UUID.randomUUID().toString(), "testdata2", "testmetadata2");
//        Event event3 = new Event(UUID.randomUUID().toString(), "testdata3", "testmetadata3");
//
//        store.appendEvent(streamId, Collections.singletonList(event1), 0);
//        store.appendEvent(streamId, Collections.singletonList(event2), 1);
//        int currentStreamVersion = store.appendEvent(streamId, Collections.singletonList(event3), 2);
//
//        System.out.println("currentStreamVersion: " + currentStreamVersion);
        List<Event> events = store.readEvents(streamId, 0, 2);
        events.forEach(eventRead -> System.out.println("event read : " + eventRead.getData()));
    }

    public int appendEvent(String streamId, List<Event> events, int expectedVersion) {

        ActorRef eventStream = actorSystem.actorOf(Props.create(EventStream.class, () -> new EventStream(streamId)));

        Object response;
        try {
            response = PatternsCS.ask(eventStream, new Append(events, expectedVersion), Duration.ofSeconds(5))
                    .toCompletableFuture()
                    .get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (response instanceof Exception) {
            throw new RuntimeException((Exception) response);
        }

        return (Integer) response;
    }

    public List<Event> readEvents(String streamId, int from, int to) throws Exception {

        ActorRef eventStream = actorSystem.actorOf(Props.create(EventStream.class, () -> new EventStream(streamId)));

        Object response = PatternsCS.ask(eventStream, new Read(from, to), Duration.ofSeconds(5)).toCompletableFuture().get();
        if (response instanceof Exception) {
            throw (Exception) response;
        }
        List<Event> events = (List<Event>) response;

        return events;
    }

//    public List<Event> readEventsUsingReadJournal(String streamId) {
//        JdbcReadJournal readJournal =
//                PersistenceQuery.get(actorSystem).getReadJournalFor(JdbcReadJournal.class, JdbcReadJournal.Identifier());
//
//        Source<EventEnvelope, NotUsed> source = readJournal.eventsByPersistenceId(streamId, 0, Long.MAX_VALUE);
//
//        ArrayList<Event> streamEvents = new ArrayList<>();
//
//        CompletionStage<Done> stage = source.runForeach(envelope -> {
//            streamEvents.add((Event) envelope.event());
//        }, actorMaterializer);
//
//        System.out.println("hello1");
//        try {
//            stage.toCompletableFuture().get();
//        } catch (InterruptedException | ExecutionException e) {
//           throw new RuntimeException(e);
//        }
//        System.out.println("hello2");
//        stage.exceptionally(e -> {
//            throw new RuntimeException(e);
//        });
//        System.out.println("hello3");
//
//        return streamEvents;
//    }
}
