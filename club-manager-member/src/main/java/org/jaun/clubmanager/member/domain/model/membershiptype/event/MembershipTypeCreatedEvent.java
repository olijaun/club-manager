package org.jaun.clubmanager.member.domain.model.membershiptype.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;

public class MembershipTypeCreatedEvent extends MembershipTypeEvent {

    private final MembershipTypeId membershipTypeId;

    public MembershipTypeCreatedEvent(MembershipTypeId membershipTypeId) {
        this.membershipTypeId = requireNonNull(membershipTypeId);
    }

    public MembershipTypeId getMembershipTypeId() {
        return membershipTypeId;
    }
}
