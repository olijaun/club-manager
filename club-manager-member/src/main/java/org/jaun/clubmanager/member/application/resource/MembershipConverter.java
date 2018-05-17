package org.jaun.clubmanager.member.application.resource;

import java.time.LocalDate;
import java.util.Currency;

import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriod;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;

public class MembershipConverter {

    public static SubscriptionPeriod toSubscriptionPeriod(SubscriptionPeriodId id, CreateSubscriptionPeriodDTO in) {
        if (in == null) {
            return null;
        }

        LocalDate startDate = LocalDate.parse(in.getStartDate());
        LocalDate endDate = LocalDate.parse(in.getEndDate());

        SubscriptionPeriod period = new SubscriptionPeriod(id, startDate, endDate);
        period.updateMetadata(in.getName(), in.getDescription());

        return period;
    }

    public static Currency toCurrency(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }
}
