package org.jaun.clubmanager.member.domain.model.member.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventType;
import org.jaun.clubmanager.member.domain.model.member.MemberId;

public class MemberCreatedEvent extends DomainEvent<MemberEventType> {

    private final MemberId memberId;
    private final String firstName;
    private final String lastName;

    public MemberCreatedEvent(MemberId memberId, String firstName, String lastName) {
        super(MemberEventType.MEMBER_CREATED);
        this.memberId = memberId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
