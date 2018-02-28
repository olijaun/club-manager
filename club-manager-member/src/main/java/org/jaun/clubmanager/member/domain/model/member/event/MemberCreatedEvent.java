package org.jaun.clubmanager.member.domain.model.member.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventType;
import org.jaun.clubmanager.member.domain.model.member.MemberId;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class MemberCreatedEvent extends DomainEvent<MemberEventType> {

    private final MemberId memberId;

    public MemberCreatedEvent(MemberId memberId) {
        super(MemberEventType.MEMBER_CREATED);
        this.memberId = requireNonNull(memberId);
    }

    public MemberId getMemberId() {
        return memberId;
    }
}
