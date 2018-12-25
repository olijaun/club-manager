package org.jaun.clubmanager.person.infra.repository;

import org.jaun.clubmanager.person.domain.model.person.Person;
import org.jaun.clubmanager.person.domain.model.person.PersonId;
import org.jaun.clubmanager.person.domain.model.person.PersonRepository;
import org.jaun.clubmanager.person.domain.model.person.event.PersonEvent;
import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.eventstore.EventType;

public class PersonRepositoryImpl extends AbstractGenericRepository<Person, PersonId, PersonEvent> implements PersonRepository {

    public PersonRepositoryImpl(EventStoreClient eventStoreClient) {
        super(eventStoreClient);
    }

    @Override
    protected String getAggregateName() {
        return "person";
    }

    @Override
    protected Person toAggregate(EventStream<PersonEvent> eventStream) {
        return new Person(eventStream);
    }

    @Override
    protected Class<? extends PersonEvent> getClassByName(EventType name) {
        return PersonEventMapping.of(name).getEventClass();
    }

    @Override
    protected EventType getNameByEvent(PersonEvent event) {
        return PersonEventMapping.of(event).getEventType();
    }
}
