package org.jaun.clubmanager.domain.model.commons;

import com.google.gson.Gson;
import org.jaun.clubmanager.eventstore.ConcurrencyException;
import org.jaun.clubmanager.eventstore.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractGenericRepositoryTest {

    private Gson gson = new Gson();
    @Mock
    private EventStoreClient eventStoreClient;

    class TestId extends Id {

        protected TestId(String value) {
            super(value);
        }
    }

    class SomethingWasDone extends DomainEvent {

        public SomethingWasDone() {
            super(EventId.generate());
        }
    }

    class TestEventSourcingAggregate extends EventSourcingAggregate<TestId, SomethingWasDone> {

        private TestId id;
        private boolean done = false;

        TestEventSourcingAggregate(TestId id) {
            this.id = id;
        }

        TestEventSourcingAggregate(EventStream<SomethingWasDone> eventStream) {
            replayEvents(eventStream);
        }

        public void doSomething() {
            apply(new SomethingWasDone());
        }

        @Override
        protected void mutate(SomethingWasDone event) {
            done = true;
        }

        @Override
        public TestId getId() {
            return id;
        }

        public boolean isDone() {
            return done;
        }
    }

    class TestRepository extends AbstractGenericRepository<TestEventSourcingAggregate, TestId, SomethingWasDone> {

        public TestRepository(EventStoreClient eventStoreClient) {
            super(eventStoreClient);
        }

        @Override
        protected String getAggregateName() {
            return "test";
        }

        @Override
        protected TestEventSourcingAggregate toAggregate(EventStream<SomethingWasDone> eventStream) {
            return new TestEventSourcingAggregate(eventStream);
        }

        @Override
        protected Class<? extends SomethingWasDone> getClassByName(EventType name) {
            return SomethingWasDone.class;
        }

        @Override
        protected EventType getNameByEvent(SomethingWasDone event) {
            return new EventType("SomethingWasDone");
        }
    }

    @Test
    void save_noChanges() throws Exception {

        // prepare
        TestId testId = new TestId("xyz");
        TestEventSourcingAggregate t = new TestEventSourcingAggregate(testId);
        TestRepository repository = new TestRepository(eventStoreClient);

        // run
        repository.save(t);

        // verify
        verifyZeroInteractions(eventStoreClient);
    }

    @Test
    void save_newStream() throws Exception {

        // prepare
        TestId testId = new TestId("xyz");
        TestEventSourcingAggregate t = new TestEventSourcingAggregate(testId);
        TestRepository repository = new TestRepository(eventStoreClient);

        t.doSomething();

        // run
        repository.save(t);

        // verify
        StreamId streamId = StreamId.parse(repository.getAggregateName() + "-" + testId.getValue());
        verify(eventStoreClient).append(eq(streamId), anyList(), eq(StreamRevision.NEW_STREAM));

        assertThat(t.getChanges().size(), is(0));
    }

    @Test
    void save_existingStream() throws Exception {

        // prepare
        TestId testId = new TestId("xyz");
        TestEventSourcingAggregate t = new TestEventSourcingAggregate(testId);
        TestRepository repository = new TestRepository(eventStoreClient);

        t.doSomething();
        repository.save(t);

        t.doSomething();

        // run
        repository.save(t);

        // verify
        StreamId streamId = StreamId.parse(repository.getAggregateName() + "-" + testId.getValue());
        verify(eventStoreClient).append(eq(streamId), anyList(), eq(StreamRevision.NEW_STREAM));
        verify(eventStoreClient).append(eq(streamId), anyList(), eq(StreamRevision.from(0)));
        assertThat(t.getChanges().size(), is(0));
    }

    @Test
    void save_concurrencyException() throws Exception {

        // prepare
        TestId testId = new TestId("xyz");
        TestEventSourcingAggregate t = new TestEventSourcingAggregate(testId);
        TestRepository repository = new TestRepository(eventStoreClient);

        doThrow(new ConcurrencyException(0, 1)).when(eventStoreClient).append(any(), anyList(), any());

        t.doSomething();

        // run
        Executable e = () -> repository.save(t);

        // verify
        assertThrows(org.jaun.clubmanager.domain.model.commons.ConcurrencyException.class, e);
    }

    @Test
    void read() throws Exception {

        // prepare
        TestId testId = new TestId("xyz");
        StreamId streamId = StreamId.parse("test-" + testId.getValue());

        SomethingWasDone event = new SomethingWasDone();

        String serializedEvent = gson.toJson(event);

        StoredEventData storedEventData = new StoredEventData.Builder()
                .streamId(streamId)
                .eventId(event.getEventId())
                .eventType(new EventType("abc"))
                .payload(serializedEvent)
                .metadata(null)
                .streamRevision(StreamRevision.from(0))
                .timestamp(Instant.now())
                .position(0).build();

        StoredEvents storedEvents = new StoredEvents(Collections.singletonList(storedEventData));
        when(eventStoreClient.read(any(), any(), any())).thenReturn(storedEvents);

        TestRepository repository = new TestRepository(eventStoreClient);

        // run
        TestEventSourcingAggregate restoredAggregate = repository.get(testId);

        // verify
        assertThat(restoredAggregate.getChanges().size(), is(0));
        assertThat(restoredAggregate.isDone(), is(true));
        assertThat(restoredAggregate.getVersion(), equalTo(0));

        verify(eventStoreClient).read(eq(streamId), eq(StreamRevision.INITIAL), eq(StreamRevision.MAXIMUM));
    }
}