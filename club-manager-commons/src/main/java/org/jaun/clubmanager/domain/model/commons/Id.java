package org.jaun.clubmanager.domain.model.commons;

import java.io.Serializable;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public abstract class Id extends ValueObject implements Serializable {
    private final String value;

    protected Id(String value) {
        this.value = requireNonNull(value);
    }

    public String getValue() {
        return value;
    }

    public static <T extends Id> T random(IdFactory<T> factory) {
        return factory.create(UUID.randomUUID().toString());
    }

    public interface IdFactory<T> {
        T create(String id);
    }
}
