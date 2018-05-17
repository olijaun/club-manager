package org.jaun.clubmanager.member.domain.model.subscriptionperiod.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;

public class SubscriptionPeriodEvent extends DomainEvent {
    public SubscriptionPeriodEvent() {
        super(EventId.generate());
    }
}
