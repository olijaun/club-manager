package org.jaun.clubmanager.eventstore.akka;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import akka.persistence.AbstractPersistentActor;

public class EventStream extends AbstractPersistentActor {

    private List<Event> events = new ArrayList<>();
    private String streamId;

    public EventStream(String streamId) {
        this.streamId = streamId;
    }

    @Override
    public Receive createReceiveRecover() {
        return receiveBuilder().match(Event.class, event -> {
            mutate(event);
            System.out.println("re-stored event: " + event);
        }).build();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(Append.class, append -> {

            if (append.getExpectedVersion() != events.size()) {
                getSender().tell(new RuntimeException(
                        "expected version " + append.getExpectedVersion() + " does not match current stream version "
                        + events.size()), getSelf());
                return;
            }

            List<Event> unpersistedEvents = append.getEvents()
                    .stream()
                    .filter(newEvent -> !events.stream().anyMatch(existingEvent -> existingEvent.getId().equals(newEvent.getId())))
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
            int toIndex = (Integer.MAX_VALUE == read.getToVersion()) ? //
                    Math.min(events.size(), Integer.MAX_VALUE) //
                    : Math.min(events.size(), read.getToVersion() + 1);

            getSender().tell(events.subList(read.getFromVersion(), toIndex), getSelf());

        }).build();
    }

    private void mutate(Event event) {
        events.add(event);
    }

    @Override
    public String persistenceId() {
        return streamId;
    }
}
