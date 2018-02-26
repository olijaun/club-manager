package org.jaun.clubmanager.domain.model.commons;

public interface EventType {
    String getName();

    default boolean is(EventType type) {
        return getName().equals(type.getName());
    }
}
