package org.jaun.clubmanager.eventstore;

import org.jaun.clubmanager.domain.model.commons.EventId;

public class EventDataFixture {

    public static EventData.Builder json() {
        return json(EventId.generate());
    }

    public static EventData.Builder json(EventId eventId) {
        return EventData.builder()
                .eventId(eventId)
                .eventType(new EventType("typeA"))
                .payload("{\"hello\":\"world\"}");
    }

    public static EventData.Builder jsonWithMetadata() {
        return json().metadata("{\"meta\":\"data\"}");
    }

    public static EventData.Builder jsonWithMetadata(EventId eventId) {
        return json(eventId).metadata("{\"meta\":\"data\"}");
    }
}