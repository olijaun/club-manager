package org.jaun.clubmanager.domain.model.commons;


import java.util.ArrayList;
import java.util.List;


public abstract class EventSourcingAggregate<T extends Id, E extends DomainEvent> extends Aggregate<T> {

    private Integer version;
    private List<E> changes = new ArrayList<>();
    private EventStream eventStream;

    protected EventSourcingAggregate() {

    }

    protected void replayEvents(EventStream<E> eventStream) {

        this.eventStream = eventStream;
        eventStream.stream().forEachOrdered(domainEvent -> {
            mutate(domainEvent);
            incrementVersion();
        });
    }


    protected EventStream getEventStream() {
        return eventStream;
    }


    protected abstract void mutate(E event);


    public abstract T getId();


    private void incrementVersion() {
        if (version == null) {
            version = 0;
        } else {
            this.version++;
        }
    }


    public Integer getVersion() {
        return version;
    }


    public List<E> getChanges() {
        return new ArrayList<>(changes);
    }


    public boolean hasChanges() {
        return !changes.isEmpty();
    }


    public void clearChanges() {
        changes.clear();
    }

    protected void apply(E event) {
        changes.add(event);
        mutate(event);
        incrementVersion();
    }
}
