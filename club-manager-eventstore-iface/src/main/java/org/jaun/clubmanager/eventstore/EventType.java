package org.jaun.clubmanager.eventstore;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

public class EventType extends ValueObject {
    private final String value;

    public EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
