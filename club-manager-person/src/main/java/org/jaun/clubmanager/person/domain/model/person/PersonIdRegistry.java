package org.jaun.clubmanager.person.domain.model.person;

import java.util.LinkedHashMap;
import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.person.domain.model.person.event.StartFromResetEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRegistryCreatedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRegistryEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRequestedEvent;

public class PersonIdRegistry extends EventSourcingAggregate<PersonIdRegistryId, PersonIdRegistryEvent> {

    private PersonIdRegistryId id;
    private LinkedHashMap<PersonIdRequestId, PersonId> generatedIds = new LinkedHashMap<>();
    private int currentId = -1;

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

    /**
     * Continue next generated number after the given latestIdUsed. If latestIdUsed is lower than the current id then this is ignored.
     *
     * @param latestIdUsed
     *         The latest id that was used. The next id generated will be higher than this one.
     */
    public void continueAfter(PersonId latestIdUsed) {
        if ((latestIdUsed.asInt() + 1) <= currentId) {
            return;
        }
        apply(new StartFromResetEvent(latestIdUsed.asInt() + 1));
    }

    protected void mutate(StartFromResetEvent currentNumberSetEvent) {
        this.currentId = currentNumberSetEvent.getNewStartFrom();
    }

    protected void mutate(PersonIdRegistryCreatedEvent event) {
        this.id = event.getPersonIdRegistryId();
        this.currentId = event.getStartFrom();
    }

    protected void mutate(PersonIdRequestedEvent personIdRequestedEvent) {

        if (generatedIds.containsKey(personIdRequestedEvent.getPersonIdRequestId())) {
            // should actually be avoided when calling requestNewId
            // this is just to be sure
            return;
        }

        PersonId newPersonId = new PersonId(String.format("P%08d", currentId));
        generatedIds.put(personIdRequestedEvent.getPersonIdRequestId(), newPersonId);
        currentId++;
    }

    @Override
    protected void mutate(PersonIdRegistryEvent event) {
        if (event instanceof PersonIdRegistryCreatedEvent) {
            mutate((PersonIdRegistryCreatedEvent) event);
        } else if (event instanceof PersonIdRequestedEvent) {
            mutate((PersonIdRequestedEvent) event);
        } else if (event instanceof StartFromResetEvent) {
            mutate((StartFromResetEvent) event);
        }
    }

    public PersonIdRegistryId getId() {
        return id;
    }
}
