package org.jaun.clubmanager.member.infra.repository;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import org.jaun.clubmanager.domain.model.commons.EventMapping;
import org.jaun.clubmanager.member.domain.model.membershipperiod.event.MembershipPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershipperiod.event.MembershipPeriodEvent;
import org.jaun.clubmanager.member.domain.model.membershipperiod.event.MembershipPeriodMetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.membershipperiod.event.MembershipPeriodSubscriptionOptionAddedEvent;

public enum MembershipPeriodEventMapping implements EventMapping {
    MEMBERSHIP_PERIOD_CREATED("MembershipPeriodCreated", MembershipPeriodCreatedEvent.class), //
    METADATA_CHANGED("MetadataChanged", MembershipPeriodMetadataChangedEvent.class),  //
    SUBSCRIPTION_OPTION_ADDED("SubscriptionOptionAdded", MembershipPeriodSubscriptionOptionAddedEvent.class);

    private final String name;
    private final Class<? extends MembershipPeriodEvent> eventClass;

    MembershipPeriodEventMapping(String name, Class<? extends MembershipPeriodEvent> eventClass) {
        this.name = requireNonNull(name);
        this.eventClass = requireNonNull(eventClass);
    }

    @Override
    public Class<? extends MembershipPeriodEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public String getEventType() {
        return name;
    }

    public static MembershipPeriodEventMapping of(String name) {
        return Stream.of(MembershipPeriodEventMapping.values())
                .filter(m -> m.getEventType().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for name " + name));
    }

    public static MembershipPeriodEventMapping of(MembershipPeriodEvent event) {
        return Stream.of(MembershipPeriodEventMapping.values())
                .filter(m -> m.getEventClass().equals(event.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for event " + event));
    }
}
