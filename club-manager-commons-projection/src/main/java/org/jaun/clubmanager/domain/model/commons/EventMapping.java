package org.jaun.clubmanager.domain.model.commons;

import org.jaun.clubmanager.eventstore.EventType;

public interface EventMapping<E extends DomainEvent> {
    Class<? extends E> getEventClass();

    EventType getEventType();
}
