package org.jaun.clubmanager.member.domain.model.member.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.member.Address;
import org.jaun.clubmanager.member.domain.model.member.MemberId;

public class AddressChangedEvent extends DomainEvent<MemberEventType> {

    private final MemberId memberId;
    private final Address address;

    public AddressChangedEvent(MemberId memberId, Address address) {
        super(MemberEventType.ADDRESS_CHANGED);
        this.memberId = memberId;
        this.address = address;
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public Address getAddress() {
        return address;
    }
}
