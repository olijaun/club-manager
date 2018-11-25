package org.jaun.clubmanager.person.domain.model.person.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;

public abstract class PersonIdRegistryEvent extends DomainEvent {
    public PersonIdRegistryEvent() {
        super(EventId.generate());
    }
}
