package org.jaun.clubmanager.member.infra.repository;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import org.jaun.clubmanager.domain.model.commons.EventMapping;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.MetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionTypeAddedEvent;

public enum SubscriptionPeriodEventMapping implements EventMapping {
    SUBSCRIPTION_PERIOD_CREATED("SubscriptionPeriodCreated", SubscriptionPeriodCreatedEvent.class), //
    METADATA_CHANGED("SubscriptionPeriodMetadataChanged", MetadataChangedEvent.class),  //
    SUBSCRIPTION_TYPE_ADDED("SubscriptionTypeAdded", SubscriptionTypeAddedEvent.class);

    private final EventType eventType;
    private final Class<? extends SubscriptionPeriodEvent> eventClass;

    SubscriptionPeriodEventMapping(String name, Class<? extends SubscriptionPeriodEvent> eventClass) {
        this.eventType = new EventType(name);
        this.eventClass = requireNonNull(eventClass);
    }

    @Override
    public Class<? extends SubscriptionPeriodEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    public static SubscriptionPeriodEventMapping of(EventType name) {
        return Stream.of(SubscriptionPeriodEventMapping.values())
                .filter(m -> m.getEventType().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for name " + name));
    }

    public static SubscriptionPeriodEventMapping of(SubscriptionPeriodEvent event) {
        return Stream.of(SubscriptionPeriodEventMapping.values())
                .filter(m -> m.getEventClass().equals(event.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for event " + event));
    }
}
