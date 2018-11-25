package org.jaun.clubmanager.person.domain.model.person.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.person.domain.model.person.PersonId;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistryId;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRequestId;
import org.jaun.clubmanager.person.domain.model.person.PersonType;

public class PersonIdRequestedEvent extends PersonIdRegistryEvent {

    private final PersonIdRegistryId personIdRegistryId;
    private final PersonIdRequestId personIdRequestId;

    public PersonIdRequestedEvent(PersonIdRegistryId personIdRegistryId, PersonIdRequestId personIdRequestId) {
        this.personIdRegistryId = requireNonNull(personIdRegistryId);
        this.personIdRequestId = requireNonNull(personIdRequestId);
    }

    public PersonIdRegistryId getPersonIdRegistryId() {
        return personIdRegistryId;
    }

    public PersonIdRequestId getPersonIdRequestId() {
        return personIdRequestId;
    }
}
