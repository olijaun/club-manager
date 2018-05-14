package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriod;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodRepository;
import org.jaun.clubmanager.member.domain.model.membershipperiod.event.MembershipPeriodEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class MembershipPeriodRepositoryImpl extends
        AbstractGenericRepository<MembershipPeriod, MembershipPeriodId, MembershipPeriodEvent> implements
        MembershipPeriodRepository {


    @Override
    protected String getAggregateName() {
        return "membershipPeriod";
    }

    @Override
    protected MembershipPeriod toAggregate(EventStream<MembershipPeriodEvent> eventStream) {
        return new MembershipPeriod(eventStream);
    }

    @Override
    protected Class<? extends MembershipPeriodEvent> getClassByName(String name) {
        return MembershipPeriodEventMapping.of(name).getEventClass();
    }

    @Override
    protected String getNameByEvent(MembershipPeriodEvent event) {
        return MembershipPeriodEventMapping.of(event).getEventType();
    }
}
