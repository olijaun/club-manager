package org.jaun.clubmanager.evenstore;

import java.util.UUID;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

public class EventId extends ValueObject {

    private final UUID uuid;

    public EventId(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public static EventId generate() {
        return new EventId(UUID.randomUUID());
    }
}
