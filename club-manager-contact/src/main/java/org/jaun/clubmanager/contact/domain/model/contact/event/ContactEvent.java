package org.jaun.clubmanager.contact.domain.model.contact.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;

public abstract class ContactEvent extends DomainEvent {
    public ContactEvent() {
        super(EventId.generate());
    }
}
