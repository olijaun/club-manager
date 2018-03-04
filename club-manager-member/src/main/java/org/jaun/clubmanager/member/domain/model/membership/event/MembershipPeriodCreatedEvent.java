package org.jaun.clubmanager.member.domain.model.membership.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;

import java.time.LocalDate;

import static java.util.Objects.requireNonNull;

public class MembershipPeriodCreatedEvent extends DomainEvent<MembershipPeriodEventType> {

    private final MembershipPeriodId membershipPeriodId;
    private final LocalDate start;
    private final LocalDate end;

    public MembershipPeriodCreatedEvent(MembershipPeriodId membershipPeriodId, LocalDate start, LocalDate end) {
        super(MembershipPeriodEventType.MEMBERSHIP_PERIOD_CREATED);
        this.membershipPeriodId = requireNonNull(membershipPeriodId);

        this.start = requireNonNull(start);
        this.end = requireNonNull(end);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start date must be before end date");
        }
    }

    public MembershipPeriodId getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }
}
