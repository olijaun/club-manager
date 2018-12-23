package org.jaun.clubmanager.eventstore.akka;

import akka.actor.ActorSystem;
import akka.persistence.inmemory.query.javadsl.InMemoryReadJournal;
import akka.persistence.query.PersistenceQuery;
import akka.persistence.query.javadsl.EventsByTagQuery;
import akka.stream.ActorMaterializer;
import akka.testkit.javadsl.TestKit;
import com.google.gson.Gson;
import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;
import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamRevision;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.fail;

class AbstractAkkaCatchUpSubscriptionTest {

    private static ActorSystem actorSystem;
    private static ActorMaterializer actorMaterializer;
    private AkkaEventStore store;
    private TestCatchupSubscription catchupSubscription;
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

    private static class TestCatchupSubscription extends AbstractAkkaCatchUpSubscription {

        private boolean eventProcessed = false;

        public TestCatchupSubscription(ActorSystem actorSystem, ActorMaterializer actorMaterializer, EventsByTagQuery eventsByTagQuery, String... categories) {
            super(actorSystem, actorMaterializer, eventsByTagQuery, categories);

            registerMapping(new EventType("typeA"), (v, r) -> update(v, toObject(r, EventA.class)));
        }

        protected void update(Long version, EventA eventA) {
            System.out.println("update called");
            eventProcessed = true;
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
        catchupSubscription = new TestCatchupSubscription(actorSystem, actorMaterializer, readJournal, "testcategory");
    }

    @Test
    void registerMapping() throws Exception {

        // prepare
        StreamId streamId = randomStreamId();
        EventA eventA = new EventA(EventId.generate(), "a event of type a");

        EventData eventData = EventData.builder()
                .payload(gson.toJson(eventA))
                .metadata(null)
                .eventId(eventA.getEventId())
                .eventType(new EventType("typeA")).build();

        store.append(streamId, Collections.singletonList(eventData), StreamRevision.NEW_STREAM);

        // run
        catchupSubscription.start();

        // verify
        for (int i = 0; i < 30; i++) {
            Thread.sleep(1000);
            if (catchupSubscription.isEventProcessed()) {
                return;
            }
        }
        fail("message was never processed");
    }

    private StreamId randomStreamId() {
        return StreamId.parse("testcategory-" + UUID.randomUUID().toString());
    }
}