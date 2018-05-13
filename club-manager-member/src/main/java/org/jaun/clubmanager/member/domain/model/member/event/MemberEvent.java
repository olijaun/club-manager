package org.jaun.clubmanager.member.domain.model.member.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventId;

public class MemberEvent extends DomainEvent {
    public MemberEvent() {
        super(EventId.generate());
    }
}
