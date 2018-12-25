package org.jaun.clubmanager.person.infra.repository;

import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistry;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistryId;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistryRepository;
import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRegistryEvent;

public class PersonIdRegistryRepositoryImpl extends
        AbstractGenericRepository<PersonIdRegistry, PersonIdRegistryId, PersonIdRegistryEvent> implements
        PersonIdRegistryRepository {

    public PersonIdRegistryRepositoryImpl( EventStoreClient eventStoreClient) {
        super(eventStoreClient);
    }

    @Override
    protected String getAggregateName() {
        return "personIdRegistry";
    }

    @Override
    protected PersonIdRegistry toAggregate(EventStream<PersonIdRegistryEvent> eventStream) {
        return new PersonIdRegistry(eventStream);
    }

    @Override
    protected Class<? extends PersonIdRegistryEvent> getClassByName(EventType name) {
        return PersonIdRegistryEventMapping.of(name).getEventClass();
    }

    @Override
    protected EventType getNameByEvent(PersonIdRegistryEvent event) {
        return PersonIdRegistryEventMapping.of(event).getEventType();
    }
}
