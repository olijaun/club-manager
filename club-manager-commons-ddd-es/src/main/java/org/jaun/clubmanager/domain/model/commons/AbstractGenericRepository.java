package org.jaun.clubmanager.domain.model.commons;


import java.util.List;
import java.util.stream.Collectors;

import org.jaun.clubmanager.eventstore.Category;
import org.jaun.clubmanager.eventstore.EventData;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StoredEventData;
import org.jaun.clubmanager.eventstore.StreamId;
import org.jaun.clubmanager.eventstore.StreamRevision;

import com.google.gson.Gson;

public abstract class AbstractGenericRepository<A extends EventSourcingAggregate<I, E>, I extends Id, E extends DomainEvent> implements
        GenericRepository<A, I> {

    private EventStoreClient eventStoreClient;
    private Gson gson = new Gson();

    protected abstract String getAggregateName();

    protected abstract A toAggregate(EventStream<E> eventStream);

    public AbstractGenericRepository(EventStoreClient eventStoreClient) {
        this.eventStoreClient = eventStoreClient;
    }

    public void save(A m) throws ConcurrencyException {

        Long expectedVersion = (long) m.getVersion() - m.getChanges().size();

        StreamId streamId = new StreamId(m.getId(), new Category(getAggregateName()));

        if (m.hasChanges()) {
            try {
                eventStoreClient.append(streamId, toEventData(m.getChanges()), StreamRevision.from(expectedVersion));
            } catch (org.jaun.clubmanager.eventstore.ConcurrencyException e) {
                throw new ConcurrencyException(e,
                        "failed to append event to stream " + streamId.getValue() + " with expected revision " + expectedVersion);
            }
            m.clearChanges();
        }
    }

    public A get(I id) {

        StreamId streamId = new StreamId(id, new Category(getAggregateName()));

        List<E> domainEventList = null;
        try {
            domainEventList = eventStoreClient.read(streamId, StreamRevision.INITIAL, StreamRevision.MAXIMUM).stream() //
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

            return EventData.builder()
                    .eventId(event.getEventId())
                    .eventType(getNameByEvent(event))
                    .payload(gson.toJson(event)).build();

        } catch (RuntimeException e) {
            throw new IllegalStateException("could not serialize event to string: " + event, e);
        }
    }

    private E toObject(StoredEventData resolvedEvent) {

        try {
            return gson.fromJson(resolvedEvent.getPayload(), getClassByName(resolvedEvent.getEventType()));
            // getEventClass(resolvedEvent.event.eventType));
        } catch (RuntimeException e) {
            throw new IllegalStateException(
                    "could not deserialize event string to object: " + resolvedEvent.getEventType().getValue(), e);
        }
    }

    protected abstract Class<? extends E> getClassByName(EventType name);

    protected abstract EventType getNameByEvent(E event);

}
