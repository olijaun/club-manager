package org.jaun.clubmanager.domain.model.commons;

public interface GenericRepository<T extends EventSourcingAggregate<I, ?>, I extends Id> {
    void save(T aggregate) throws ConcurrencyException;

    T get(I id);
}
