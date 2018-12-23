package org.jaun.clubmanager.eventstore.akka;

import akka.persistence.journal.Tagged;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaggingEventAdapterTest {

    private TaggingEventAdapter taggingEventAdapter;

    @BeforeEach
    void setUp() {
        this.taggingEventAdapter = new TaggingEventAdapter();
    }

    @Test
    void manifest() {

        // run
        String manifest = taggingEventAdapter.manifest(new Object());

        // verify
        assertThat(manifest, CoreMatchers.equalTo(""));
    }

    @Test
    void toJournal() {

        // prepare
        EventDataWithStreamId eventDataWithStreamId = EventDataWithStreamIdFixture.eventDataWithStreamId();

        // run
        Tagged tagged = (Tagged) taggingEventAdapter.toJournal(eventDataWithStreamId);

        // verify
        assertThat(tagged.payload(), is(eventDataWithStreamId));
        assertThat(tagged.tags().size(), is(2));
        assertTrue(tagged.tags().contains("eventType." + eventDataWithStreamId.getEventType().getValue()));
        assertTrue(tagged.tags().contains("category." + eventDataWithStreamId.getStreamId().getCategory().get().getName()));
    }

    @Test
    void toJournal_unknownEvent() {

        // prepare
        SomeEvent someEvent = new SomeEvent();

        // run
        SomeEvent someEventTagged = (SomeEvent) taggingEventAdapter.toJournal(someEvent);

        // verify
        assertThat(someEventTagged, is(someEvent));
    }

    private static class SomeEvent {

    }
}