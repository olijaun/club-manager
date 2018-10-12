package org.jaun.clubmanager.contact.infra.repository;

import org.jaun.clubmanager.contact.domain.model.contact.Person;
import org.jaun.clubmanager.contact.domain.model.contact.PersonId;
import org.jaun.clubmanager.contact.domain.model.contact.PersonRepository;
import org.jaun.clubmanager.contact.domain.model.contact.event.PersonEvent;
import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.eventstore.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PersonRepositoryImpl extends AbstractGenericRepository<Person, PersonId, PersonEvent> implements PersonRepository {

    public PersonRepositoryImpl(@Autowired EventStoreClient eventStoreClient) {
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
