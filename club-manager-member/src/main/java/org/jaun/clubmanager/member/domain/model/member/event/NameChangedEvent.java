package org.jaun.clubmanager.member.domain.model.member.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.member.MemberId;

import static java.util.Objects.requireNonNull;

public class NameChangedEvent extends DomainEvent<MemberEventType> {

    private final MemberId memberId;
    private final String firstName;
    private final String lastName;

    public NameChangedEvent(MemberId memberId, String firstName, String lastName) {
        super(MemberEventType.NAME_CHANGED);
        this.memberId = requireNonNull(memberId);
        this.firstName = firstName;
        this.lastName = requireNonNull(lastName);
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
