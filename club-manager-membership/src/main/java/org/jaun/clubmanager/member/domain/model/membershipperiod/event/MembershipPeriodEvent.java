package org.jaun.clubmanager.member.domain.model.membershipperiod.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;

public class MembershipPeriodEvent extends DomainEvent {
    public MembershipPeriodEvent() {
        super(EventId.generate());
    }
}
