package org.jaun.clubmanager.member.infra.repository;


import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.domain.model.commons.EventType;
import org.jaun.clubmanager.domain.model.commons.GenericRepository;
import org.jaun.clubmanager.domain.model.commons.Id;
import org.jaun.clubmanager.domain.model.commons.StreamId;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import com.github.msemys.esjc.ExpectedVersion;
import com.github.msemys.esjc.ResolvedEvent;
import com.google.gson.Gson;


public abstract class AbstractGenericRepository<T extends EventSourcingAggregate<I>, I extends Id> implements
        GenericRepository<T, I> {

    private EventStore eventStore;
    private Gson gson = new Gson();

    protected abstract String getAggregateName();

    protected abstract T toAggregate(EventStream<T> eventStream);

    public AbstractGenericRepository() {
        this.eventStore =
                EventStoreBuilder.newBuilder().singleNodeAddress("127.0.0.1", 1113).userCredentials("admin", "changeit").build();
    }

    public void save(T m) throws ConcurrencyException {

        Long expectedVersion = (long) m.getVersion() - m.getChanges().size();

        if (expectedVersion == -1) {
            expectedVersion = ExpectedVersion.ANY;
        }

        if (m.hasChanges()) {
            eventStore.appendToStream(new StreamId(m.getId(), getAggregateName()).getValue(), expectedVersion,
                    toEventData(m.getChanges()));
            m.clearChanges();
        }
    }

    public T get(I id) {

        StreamId streamId = new StreamId(id, getAggregateName());

        List<DomainEvent> domainEventList = null;
        try {
            domainEventList = eventStore.streamEventsForward(streamId.getValue(), 0, 4096, false) //
                    .map(e -> toObject(e)).collect(Collectors.toList());

        } catch (Exception e) {
            return null;
        }

        if (domainEventList.isEmpty()) {
            return null;
        }

        return toAggregate(new EventStream<T>(streamId, domainEventList));
    }


    private List<EventData> toEventData(List<DomainEvent> events) {
        return events.stream().map(this::toEventData).collect(Collectors.toList());
    }


    private EventData toEventData(DomainEvent event) {

        try {

            return EventData.newBuilder() //
                    .eventId(event.getEventId().getUuid()) //
                    .type(event.getEventType().getName()) //
                    .jsonData(gson.toJson(event)).build();

        } catch (RuntimeException e) {
            throw new IllegalStateException("could not serialize event to string: " + event, e);
        }
    }

    private DomainEvent toObject(ResolvedEvent resolvedEvent) {

        try {
            return gson.fromJson(new String(resolvedEvent.event.data, "UTF-8"), getEventClass(() -> resolvedEvent.event.eventType));
            // getEventClass(resolvedEvent.event.eventType));
        } catch (RuntimeException | UnsupportedEncodingException e) {
            throw new IllegalStateException("could not deserialize event string to object: " + resolvedEvent.event.eventType, e);
        }
    }


//    private DomainEvent toObject(EventData eventData) {
//
//        try {
//            return gson.fromJson(eventData.getPayload(), getEventClass(EventType.A));
//        } catch (RuntimeException e) {
//            throw new IllegalStateException("could not deserialize event string to object: " + eventData.getEventType(), e);
//        }
//    }

    protected abstract Class<? extends DomainEvent> getEventClass(EventType eventType);

}
