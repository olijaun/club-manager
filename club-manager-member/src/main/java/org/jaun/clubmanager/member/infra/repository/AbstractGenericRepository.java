package org.jaun.clubmanager.member.infra.repository;


import com.github.msemys.esjc.AllEventsSlice;
import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.Position;
import com.google.gson.Gson;
import org.jaun.clubmanager.domain.model.commons.*;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


public abstract class AbstractGenericRepository<T extends EventSourcingAggregate<I>, I extends Id> implements GenericRepository<T, I> {

    @Inject
    private EventStore eventStore;
    private Gson gson = new Gson();

    protected abstract String getAggregateName();

    protected abstract T toAggregate(EventStream<T> eventStream);

    public void save(T m) throws ConcurrencyException {

        Integer expectedVersion = m.getVersion() - m.getChanges().size();

        if (expectedVersion == -1) {
            expectedVersion = null;
        }

        if (m.hasChanges()) {
            eventStore.appendToStream(new StreamId(m.getId(), getAggregateName()).getValue(), expectedVersion, toEventData(m.getChanges()));
            m.clearChanges();
        }
    }

    public T get(I id) {

        StreamId streamId = new StreamId(id, getAggregateName());

        List<EventData> eventDataList = null;
        try {
            eventStore.readAllEventsForward(Position.START, Integer.MAX_VALUE, false).thenAccept(e ->
                    e.events.forEach(i -> toObject(i))
                            new String(i.originalEvent().data))));
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }

        List<EventData> eventDataList = eventStore.readAllEventsForward(streamId.getValue(), 1, false);

        if (eventDataList.isEmpty()) {
            return null;
        }

        List<DomainEvent> eventList = eventDataList.stream().map(this::toObject).collect(Collectors.toList());
        return toAggregate(new EventStream(streamId, eventList));
    }


    private List<EventData> toEventData(List<DomainEvent> events) {
        return events.stream().map(this::toEventData).collect(Collectors.toList());
    }


    private EventData toEventData(DomainEvent event) {

        try {

            return EventData.newBuilder() //
                    .eventId(event.getEventId().getUuid()) //
                    .type(event.getEventType().name()) // TODO
                    .jsonData(gson.toJson(event)).build();

        } catch (RuntimeException e) {
            throw new IllegalStateException("could not serialize event to string: " + event, e);
        }
    }


    private DomainEvent toObject(EventData eventData) {

        try {
            return gson.fromJson(eventData.getPayload(), getEventClass(eventData.getEventType()));
        } catch (RuntimeException e) {
            throw new IllegalStateException("could not deserialize event string to object: " + eventData.getEventType(), e);
        }
    }

    protected abstract Class<? extends DomainEvent> getEventClass(EventType eventType);

}
