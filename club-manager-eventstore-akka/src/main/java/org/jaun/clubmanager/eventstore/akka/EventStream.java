package org.jaun.clubmanager.eventstore.akka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jaun.clubmanager.eventstore.ConcurrencyException;
import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamRevision;

import akka.persistence.AbstractPersistentActor;

public class EventStream extends AbstractPersistentActor {

    private List<EventData> events = new ArrayList<>();
    private final StreamId streamId;

    public EventStream(StreamId streamId) {
        this.streamId = streamId;
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder().match(EventData.class, event -> {
            mutate(event);
            System.out.println("re-stored event: " + event);
        }).build();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Append.class, append -> {

            if (!append.getExpectedVersion().equals(StreamRevision.UNSPECIFIED) //
                    && append.getExpectedVersion().getValue() != (events.size() - 1)) {

                getSender().tell(new ConcurrencyException(append.getExpectedVersion().getValue(), (events.size() - 1)), getSelf());
                return;
            }

            List<EventDataWithStreamId> unpersistedEvents = append.getEvents()
                    .stream()
                    .filter(newEvent -> !events.stream()
                            .anyMatch(storedEventData -> storedEventData.getEventId().equals(newEvent.getEventId())))
                    .map(e -> new EventDataWithStreamId(streamId, e))
                    .collect(Collectors.toList());

            if (unpersistedEvents.isEmpty()) {
                Integer currentStreamVersion = events.size() - 1;
                getSender().tell(currentStreamVersion, getSelf());
                return;
            }

            AtomicInteger atomicInteger = new AtomicInteger(0);
            persistAll(unpersistedEvents, (event) -> {
                mutate(event);
                if (atomicInteger.incrementAndGet() >= unpersistedEvents.size()) {
                    Integer currentStreamVersion = events.size() - 1;
                    getSender().tell(currentStreamVersion, getSelf());
                }
            });

        }).match(Read.class, read -> {

            // sublist to is exclusive
            int toIndex = (Long.valueOf(Integer.MAX_VALUE) <= read.getToVersion().getValue().longValue()) ? //
                    Math.min(events.size(), Integer.MAX_VALUE) //
                    : Math.min(events.size(), read.getToVersion().getValue().intValue() + 1);

            List<EventData> list = events.subList(read.getFromVersion().getValue().intValue(), toIndex);

            getSender().tell(list, getSelf());

        }).match(QueryLength.class, queryLength -> {

            getSender().tell(events.size(), getSelf());

        }).build();
    }

    private void mutate(EventData eventData) {
        events.add(eventData);
    }

    @Override
    public String persistenceId() {
        return streamId.getValue();
    }
}
