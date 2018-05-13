package org.jaun.clubmanager.member.domain.model.membershipperiod.event;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;

public class MembershipPeriodMetadataChangedEvent extends MembershipPeriodEvent {

    private final String name;
    private final String description;
    private final MembershipPeriodId membershipPeriodId;

    public MembershipPeriodMetadataChangedEvent(MembershipPeriodId membershipPeriodId, String name, String description) {
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
