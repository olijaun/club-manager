package org.jaun.clubmanager.domain.model.commons;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DomainEventTest {

    @Test
    void getEventId() {
        EventId eventId = EventId.generate();
        DomainEvent domainEvent = new DomainEvent(eventId);

        assertThat(domainEvent.getEventId(), equalTo(eventId));
    }

    @Test
    void nullId() {
        Executable e = () -> new DomainEvent(null);

        assertThrows(NullPointerException.class, e);
    }
}