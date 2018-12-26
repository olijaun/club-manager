package org.jaun.clubmanager.eventstore.akka;

import akka.actor.ActorSystem;
import akka.persistence.inmemory.query.javadsl.InMemoryReadJournal;
import akka.persistence.query.PersistenceQuery;
import akka.stream.ActorMaterializer;
import akka.testkit.javadsl.TestKit;
import com.google.gson.Gson;
import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;
import org.jaun.clubmanager.eventstore.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.fail;

class AkkaStreamReaderTest {

    private static ActorSystem actorSystem;
    private static ActorMaterializer actorMaterializer;
    private AkkaEventStore store;
    private AkkaStreamReader akkaStreamReader;
    private final Gson gson = new Gson();

    private static class EventA extends DomainEvent {

        private final String attribute;

        public EventA(EventId eventId, String attribute) {
            super(eventId);
            this.attribute = requireNonNull(attribute);
        }

        public String getAttribute() {
            return attribute;
        }
    }

    private static class TestCatchUpSubscriptionListener implements CatchUpSubscriptionListener {

        private boolean eventProcessed = false;

        public void onEvent(StoredEventData eventData) {
            System.out.println("onEvent called");
            this.eventProcessed = true;
        }

        public void onClose(CatchUpSubscription subscription, Optional<Exception> e) {

        }

        public Collection<Category> categories() {
            return Collections.singleton(new Category("testcategory"));
        }

        public boolean isEventProcessed() {
            return eventProcessed;
        }
    }

    @BeforeAll
    public static void setup() {
        actorSystem = ActorSystem.create();
        actorMaterializer = ActorMaterializer.create(actorSystem);
    }

    @AfterAll
    public static void teardown() {
        TestKit.shutdownActorSystem(actorSystem);
        actorSystem = null;
    }

    @BeforeEach
    public void setUp() {
        store = new AkkaEventStore(actorSystem);
        InMemoryReadJournal readJournal = PersistenceQuery.get(actorSystem).getReadJournalFor(InMemoryReadJournal.class, InMemoryReadJournal.Identifier());
        akkaStreamReader = new AkkaStreamReader(actorMaterializer, readJournal);
    }

    @Test
    void subscribe() throws Exception {

        // prepare
        StreamId streamId = randomStreamId();
        EventA eventA = new EventA(EventId.generate(), "a event of type a");

        EventData eventData = EventData.builder()
                .payload(gson.toJson(eventA))
                .metadata(null)
                .eventId(eventA.getEventId())
                .eventType(new EventType("typeA")).build();

        store.append(streamId, Collections.singletonList(eventData), StreamRevision.NEW_STREAM);

        TestCatchUpSubscriptionListener listener = new TestCatchUpSubscriptionListener();

        // run
        akkaStreamReader.subscribe(listener);

        // verify
        for (int i = 0; i < 30; i++) {
            Thread.sleep(1000);
            if (listener.isEventProcessed()) {
                return;
            }
        }
        fail("message was never processed");
    }

    private StreamId randomStreamId() {
        return StreamId.parse("testcategory-" + UUID.randomUUID().toString());
    }
}