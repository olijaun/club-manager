package org.jaun.clubmanager.member.domain.model.subscriptionperiod.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;

public class MetadataChangedEvent extends SubscriptionPeriodEvent {

    private final String name;
    private final String description;
    private final SubscriptionPeriodId subscriptionPeriodId;

    public MetadataChangedEvent(SubscriptionPeriodId subscriptionPeriodId, String name, String description) {
        this.subscriptionPeriodId = subscriptionPeriodId;
        this.name = requireNonNull(name);
        this.description = description;
    }

    public SubscriptionPeriodId getSubscriptionPeriodId() {
        return subscriptionPeriodId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
