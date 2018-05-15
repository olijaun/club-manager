package org.jaun.clubmanager.member.domain.model.member.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.member.domain.model.member.MemberId;

public class MemberCreatedEvent extends MemberEvent {

    private final MemberId memberId;

    public MemberCreatedEvent(MemberId memberId) {

        this.memberId = requireNonNull(memberId);
    }

    public MemberId getMemberId() {
        return memberId;
    }
}
