package org.jaun.clubmanager.domain.model.commons;

import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.eventstore.StreamId;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EventSourcingAggregateTest {

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

    @Test
    void basicTest() {
        TestId id = new TestId("123");
        TestEventSourcingAggregate aggregate = new TestEventSourcingAggregate(id);

        aggregate.doSomething();

        assertThat(aggregate.getId(), equalTo(id));
        assertThat(aggregate.isDone(), is(true));
        assertThat(aggregate.getChanges().size(), is(1));
        SomethingWasDone somethingWasDone = aggregate.getChanges().get(0);
        assertNotNull(somethingWasDone);
    }

    @Test
    void basicTest_nothingDone() {

        TestEventSourcingAggregate aggregate = new TestEventSourcingAggregate(new TestId("123"));

        assertThat(aggregate.isDone(), is(false));
        assertThat(aggregate.getChanges().size(), is(0));
    }

    @Test
    void replayEvents() {
        SomethingWasDone event = new SomethingWasDone();
        EventStream<SomethingWasDone> stream = new EventStream<>(StreamId.parse("bla-1"), Collections.singletonList(event));
        TestEventSourcingAggregate aggregate = new TestEventSourcingAggregate(stream);

        assertThat(aggregate.isDone(), is(true));
        assertThat(aggregate.getChanges().size(), is(0));
    }
}