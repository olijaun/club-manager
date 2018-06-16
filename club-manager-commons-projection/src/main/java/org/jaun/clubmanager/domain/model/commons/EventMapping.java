package org.jaun.clubmanager.domain.model.commons;

public interface EventMapping<E extends DomainEvent> {
    Class<? extends E> getEventClass();

    String getEventType();
}
