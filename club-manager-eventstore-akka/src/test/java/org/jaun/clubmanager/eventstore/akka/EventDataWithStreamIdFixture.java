package org.jaun.clubmanager.eventstore.akka;

import org.jaun.clubmanager.domain.model.commons.EventId;
import org.jaun.clubmanager.eventstore.EventDataFixture;
import org.jaun.clubmanager.eventstore.StreamId;

import java.util.UUID;

class EventDataWithStreamIdFixture {

    public static EventDataWithStreamId eventDataWithStreamId(EventId eventId) {
        return new EventDataWithStreamId(StreamId.parse("teststream-" + UUID.randomUUID()), EventDataFixture.jsonWithMetadata().build());
    }

    public static EventDataWithStreamId eventDataWithStreamId() {
        return new EventDataWithStreamId(StreamId.parse("teststream-" + UUID.randomUUID()), EventDataFixture.jsonWithMetadata().build());
    }
}