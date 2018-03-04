package org.jaun.clubmanager.member.domain.model.membership.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;

import java.time.Period;

import static java.util.Objects.requireNonNull;

public class MembershipPeriodMetadataChangedEvent extends DomainEvent<MembershipPeriodEventType> {

    private final String name;
    private final String description;
    private final MembershipPeriodId membershipPeriodId;

    public MembershipPeriodMetadataChangedEvent(MembershipPeriodId membershipPeriodId, String name, String description) {
        super(MembershipPeriodEventType.METADATA_CHANGED);
        this.membershipPeriodId = membershipPeriodId;
        this.name = requireNonNull(name);
        this.description = description;
    }

    public MembershipPeriodId getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
