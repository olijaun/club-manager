package org.jaun.clubmanager.person.domain.model.person.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.person.domain.model.person.PersonId;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistryId;
import org.jaun.clubmanager.person.domain.model.person.PersonType;

public class PersonIdRegistryCreatedEvent extends PersonIdRegistryEvent {

    private final PersonIdRegistryId personIdRegistryId;
    private final int startFrom;

    public PersonIdRegistryCreatedEvent(PersonIdRegistryId personIdRegistryId, int startFrom) {
        this.personIdRegistryId = requireNonNull(personIdRegistryId);
        this.startFrom = startFrom;
    }

    public PersonIdRegistryId getPersonIdRegistryId() {
        return personIdRegistryId;
    }

    public int getStartFrom() {
        return startFrom;
    }
}
