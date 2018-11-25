package org.jaun.clubmanager.person.domain.model.person;

import java.util.LinkedHashMap;
import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRegistryCreatedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRegistryEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRequestedEvent;

public class PersonIdRegistry extends EventSourcingAggregate<PersonIdRegistryId, PersonIdRegistryEvent> {

    private PersonIdRegistryId id;
    private int startFrom;
    private LinkedHashMap<PersonIdRequestId, PersonId> generatedIds = new LinkedHashMap<>();

    public PersonIdRegistry(PersonIdRegistryId id, int startFrom) {
        apply(new PersonIdRegistryCreatedEvent(id, startFrom));
    }

    public PersonIdRegistry(EventStream<PersonIdRegistryEvent> eventStream) {
        replayEvents(eventStream);
    }

    public Optional<PersonId> getPersonIdByRequestId(PersonIdRequestId personIdRequestId) {

        return Optional.ofNullable(generatedIds.get(personIdRequestId));
    }

    public void requestId(PersonIdRequestId personIdRequestId) {

        if (generatedIds.containsKey(personIdRequestId)) {
            // be idempotent
            return;
        }

        apply(new PersonIdRequestedEvent(id, personIdRequestId));
    }

    protected void mutate(PersonIdRegistryCreatedEvent event) {
        this.id = event.getPersonIdRegistryId();
        this.startFrom = event.getStartFrom();
    }

    protected void mutate(PersonIdRequestedEvent personIdRequestedEvent) {

        if(generatedIds.containsKey(personIdRequestedEvent.getPersonIdRequestId())) {
            // should actually be avoided when calling requestNewId
            // this is just to be sure
            return;
        }
        int newNumber = startFrom + generatedIds.size();
        PersonId newPersonId = new PersonId(String.format("P%08d", newNumber));
        generatedIds.put(personIdRequestedEvent.getPersonIdRequestId(), newPersonId);
    }

    @Override
    protected void mutate(PersonIdRegistryEvent event) {
        if (event instanceof PersonIdRegistryCreatedEvent) {
            mutate((PersonIdRegistryCreatedEvent) event);
        } else if (event instanceof PersonIdRequestedEvent) {
            mutate((PersonIdRequestedEvent) event);
        }
    }


    public PersonIdRegistryId getId() {
        return id;
    }
}
