package org.jaun.clubmanager.member.domain.model.membership.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;

public class MembershipEvent extends DomainEvent {
    public MembershipEvent() {
        super(EventId.generate());
    }
}
