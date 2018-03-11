package org.jaun.clubmanager.member.infra.repository;

import java.util.stream.Stream;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.domain.model.commons.EventType;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriod;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodRepository;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodEventType;
import org.springframework.stereotype.Service;

@Service
public class MembershipPeriodRepositoryImpl extends AbstractGenericRepository<MembershipPeriod, MembershipPeriodId> implements
        MembershipPeriodRepository {


    @Override
    protected String getAggregateName() {
        return "membershipPeriod";
    }

    @Override
    protected MembershipPeriod toAggregate(EventStream<MembershipPeriod> eventStream) {
        return new MembershipPeriod(eventStream);
    }

    @Override
    protected Class<? extends DomainEvent> getEventClass(EventType evenType) {
        return Stream.of(MembershipPeriodEventType.values())
                .filter(et -> et.getName().equals(evenType.getName()))
                .map(MembershipPeriodEventType::getEventClass)
                .findFirst()
                .get();
    }
}
