package org.jaun.clubmanager.member.domain.model.membershipperiod.event;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;

public class MembershipPeriodCreatedEvent extends MembershipPeriodEvent {

    private final MembershipPeriodId membershipPeriodId;
    private final LocalDate start;
    private final LocalDate end;

    public MembershipPeriodCreatedEvent(MembershipPeriodId membershipPeriodId, LocalDate start, LocalDate end) {
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
