package org.jaun.clubmanager.domain.model.commons;

import java.util.UUID;

public class EventId extends Id {

    public EventId(UUID uuid) {
        super(uuid.toString());
    }

    public UUID getUuid() {
        return UUID.fromString(getValue());
    }

    public static EventId generate() {
        return new EventId(UUID.randomUUID());
    }

    public static final EventId parse(String uuidAsString) {
        return new EventId(UUID.fromString(uuidAsString));
    }
}
