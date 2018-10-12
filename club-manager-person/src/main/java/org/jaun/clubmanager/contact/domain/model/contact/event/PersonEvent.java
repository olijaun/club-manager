package org.jaun.clubmanager.contact.domain.model.contact.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;

public abstract class PersonEvent extends DomainEvent {
    public PersonEvent() {
        super(EventId.generate());
    }
}
