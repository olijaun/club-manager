package org.jaun.clubmanager.member.domain.model.membership.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;

import java.time.Period;

import static java.util.Objects.requireNonNull;

public class MembershipPeriodCreatedEvent extends DomainEvent<MembershipPeriodEventType> {

    private final MembershipPeriodId membershipPeriodId;
    private final Period period;

    public MembershipPeriodCreatedEvent(MembershipPeriodId membershipPeriodId, Period period) {
        super(MembershipPeriodEventType.MEMBERSHIP_PERIOD_CREATED);
        this.membershipPeriodId = requireNonNull(membershipPeriodId);
        this.period = requireNonNull(period);
    }

    public MembershipPeriodId getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public Period getPeriod() {
        return period;
    }
}
