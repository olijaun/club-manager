package org.jaun.clubmanager.eventstore.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.StreamId;

import akka.persistence.AbstractPersistentActor;

public class EventStream extends AbstractPersistentActor {

    private List<EventData> events = new ArrayList<>();
    private StreamId streamId;

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

            if (append.getExpectedVersion().getValue() != events.size()) {
                getSender().tell(new RuntimeException(
                        "expected version " + append.getExpectedVersion() + " does not match current stream version "
                        + events.size()), getSelf());
                return;
            }

            List<EventData> unpersistedEvents = append.getEvents()
                    .stream()
                    .filter(newEvent -> !events.stream()
                            .anyMatch(storedEventData -> storedEventData.getEventId().equals(newEvent.getEventId())))
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
            int toIndex = (Integer.MAX_VALUE == read.getToVersion().getValue().intValue()) ? //
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
