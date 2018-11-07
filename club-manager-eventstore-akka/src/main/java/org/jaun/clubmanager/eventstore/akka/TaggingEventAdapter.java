package org.jaun.clubmanager.eventstore.akka;

import java.util.HashSet;

import akka.persistence.journal.Tagged;
import akka.persistence.journal.WriteEventAdapter;

public class TaggingEventAdapter implements WriteEventAdapter {

    @Override
    public String manifest(Object event) {
        return "";
    }

    @Override
    public Object toJournal(Object event) {
        if (event instanceof EventDataWithStreamId) {
            System.out.println("tagging event: " + event);
            EventDataWithStreamId eventData = (EventDataWithStreamId) event;

            HashSet<String> tags = new HashSet<>();

            String eventType = "eventType." + eventData.getEventType().getValue();

            tags.add(eventType);

            eventData.getStreamId().getCategory().ifPresent(c -> tags.add("category." + c.getName()));

            return new Tagged(eventData, tags);
        }
        return event;
    }
}
