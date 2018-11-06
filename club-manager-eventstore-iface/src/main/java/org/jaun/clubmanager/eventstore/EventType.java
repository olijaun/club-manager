package org.jaun.clubmanager.eventstore;

import java.io.Serializable;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

public class EventType extends ValueObject implements Serializable {
    private final String value;

    public EventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
