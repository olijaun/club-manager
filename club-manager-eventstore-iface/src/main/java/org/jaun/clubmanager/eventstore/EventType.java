package org.jaun.clubmanager.eventstore;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

import java.io.Serializable;

import static java.util.Objects.requireNonNull;

public class EventType extends ValueObject implements Serializable {
    private final String value;

    public EventType(String value) {
        this.value = requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
