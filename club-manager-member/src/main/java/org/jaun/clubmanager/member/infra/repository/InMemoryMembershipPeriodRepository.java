package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.member.domain.model.membership.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;

import static java.util.Arrays.asList;

@Service
public class InMemoryMembershipPeriodRepository implements MembershipPeriodRepository {

    public Collection<MembershipPeriod> getAll() {

        Currency currency = Currency.getInstance(new Locale("de", "CH"));


        SubscriptionDefinition definition1 = new SubscriptionDefinition(new SubscriptionDefinitionId("1"), new MembershipTypeId("1"), "Normal 2017", 90., currency);
        Period period2017 = Period.between(LocalDate.of(2017, 01, 01), LocalDate.of(2017, 12, 31));
        MembershipPeriod m1 = new MembershipPeriod(new MembershipPeriodId("1"), period2017, "2017", "Vereinsjahr 2017", asList(definition1));


        SubscriptionDefinition definition2 = new SubscriptionDefinition(new SubscriptionDefinitionId("1"), new MembershipTypeId("2"), "Normal 2018", 90., currency);
        SubscriptionDefinition definition3 = new SubscriptionDefinition(new SubscriptionDefinitionId("2"), new MembershipTypeId("1"), "Gönner 2018", 150., currency);
        Period period2018 = Period.between(LocalDate.of(2018, 01, 01), LocalDate.of(2018, 12, 31));
        MembershipPeriod m2 = new MembershipPeriod(new MembershipPeriodId("2"), period2018, "2018", "Vereinsjahr 2018", asList(definition2, definition3));

        return asList(m1, m2);
    }
}
