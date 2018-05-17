package org.jaun.clubmanager.member.domain.model.subscriptionperiod.event;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;

import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;

public class SubscriptionPeriodCreatedEvent extends SubscriptionPeriodEvent {

    private final SubscriptionPeriodId subscriptionPeriodId;
    private final LocalDate start;
    private final LocalDate end;

    public SubscriptionPeriodCreatedEvent(SubscriptionPeriodId subscriptionPeriodId, LocalDate start, LocalDate end) {
        this.subscriptionPeriodId = requireNonNull(subscriptionPeriodId);

        this.start = requireNonNull(start);
        this.end = requireNonNull(end);

        if (start.isAfter(end)) {
            throw new IllegalArgumentException("start date must be before end date");
        }
    }

    public SubscriptionPeriodId getSubscriptionPeriodId() {
        return subscriptionPeriodId;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }
}
