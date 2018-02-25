package org.jaun.clubmanager.domain.model.commons;

import java.util.UUID;

public class EventId {
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
