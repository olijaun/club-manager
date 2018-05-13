package org.jaun.clubmanager.member.application.resource;

import java.time.LocalDate;
import java.util.Currency;

import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriod;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;

public class MembershipConverter {

    public static MembershipPeriod toMembershipPeriod(MembershipPeriodDTO in) {
        if (in == null) {
            return null;
        }

        LocalDate startDate = LocalDate.parse(in.getStartDate());
        LocalDate endDate = LocalDate.parse(in.getEndDate());

        MembershipPeriod period = new MembershipPeriod(MembershipPeriodId.random(MembershipPeriodId::new), startDate, endDate);
        period.updateMetadata(in.getName(), in.getDescription());

        return period;
    }

    public static Currency toCurrency(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }
}
