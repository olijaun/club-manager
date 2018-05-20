package org.jaun.clubmanager.invoice.domain.model.invoice.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;

public class InvoiceEvent extends DomainEvent {
    public InvoiceEvent() {
        super(EventId.generate());
    }
}
