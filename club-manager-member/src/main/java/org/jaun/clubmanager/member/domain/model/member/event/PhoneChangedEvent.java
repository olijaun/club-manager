package org.jaun.clubmanager.member.domain.model.member.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.member.Address;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.PhoneNumber;

import static java.util.Objects.requireNonNull;

public class PhoneChangedEvent extends DomainEvent<MemberEventType> {

    private final MemberId memberId;
    private final PhoneNumber phoneNumber;

    public PhoneChangedEvent(MemberId memberId, PhoneNumber phoneNumber) {
        super(MemberEventType.PHONE_CHANGED);
        this.memberId = requireNonNull(memberId);
        this.phoneNumber = phoneNumber;
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }
}
