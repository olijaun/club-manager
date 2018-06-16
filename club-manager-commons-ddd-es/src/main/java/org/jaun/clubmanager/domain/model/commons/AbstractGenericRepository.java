package org.jaun.clubmanager.domain.model.commons;


import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import org.jaun.clubmanager.eventstore.Category;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.eventstore.StreamId;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import com.github.msemys.esjc.ExpectedVersion;
import com.github.msemys.esjc.ResolvedEvent;
import com.google.gson.Gson;


public abstract class AbstractGenericRepository<A extends EventSourcingAggregate<I, E>, I extends Id, E extends DomainEvent> implements
        GenericRepository<A, I> {

    private EventStore eventStore;
    private Gson gson = new Gson();

    protected abstract String getAggregateName();

    protected abstract A toAggregate(EventStream<E> eventStream);

    public AbstractGenericRepository() {
        this.eventStore =
                EventStoreBuilder.newBuilder().singleNodeAddress("127.0.0.1", 1113).userCredentials("admin", "changeit").build();
    }

    public void save(A m) throws ConcurrencyException {

        Long expectedVersion = (long) m.getVersion() - m.getChanges().size();

        if (expectedVersion == -1) {
            expectedVersion = ExpectedVersion.ANY;
        }

        if (m.hasChanges()) {
            eventStore.appendToStream(new StreamId(m.getId(), new Category(getAggregateName())).getValue(), expectedVersion,
                    toEventData(m.getChanges()));
            m.clearChanges();
        }
    }

    public A get(I id) {

        StreamId streamId = new StreamId(id, new Category(getAggregateName()));

        List<E> domainEventList = null;
        try {
            domainEventList = eventStore.streamEventsForward(streamId.getValue(), 0, 4096, false) //
                    .map(e -> toObject(e)).collect(Collectors.toList());

        } catch (Exception e) {
            return null;
        }

        if (domainEventList.isEmpty()) {
            return null;
        }

        return toAggregate(new EventStream<E>(streamId, domainEventList));
    }


    private List<EventData> toEventData(List<E> events) {
        return events.stream().map(this::toEventData).collect(Collectors.toList());
    }


    private EventData toEventData(E event) {

        try {

            return EventData.newBuilder() //
                    .eventId(event.getEventId().getUuid()) //
                    .type(getNameByEvent(event)) //
                    .jsonData(gson.toJson(event)).build();

        } catch (RuntimeException e) {
            throw new IllegalStateException("could not serialize event to string: " + event, e);
        }
    }

    private E toObject(ResolvedEvent resolvedEvent) {

        try {
            return gson.fromJson(new String(resolvedEvent.event.data, "UTF-8"), getClassByName(resolvedEvent.event.eventType));
            // getEventClass(resolvedEvent.event.eventType));
        } catch (RuntimeException | UnsupportedEncodingException e) {
            throw new IllegalStateException("could not deserialize event string to object: " + resolvedEvent.event.eventType, e);
        }
    }

    protected abstract Class<? extends E> getClassByName(String name);

    protected abstract String getNameByEvent(E event);

}
