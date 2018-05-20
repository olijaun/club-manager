package org.jaun.clubmanager.member.domain.model.membershiptype.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;

public class MembershipTypeEvent extends DomainEvent {
    public MembershipTypeEvent() {
        super(EventId.generate());
    }
}
