package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.domain.model.commons.EventType;
import org.jaun.clubmanager.member.domain.model.membership.Membership;
import org.jaun.clubmanager.member.domain.model.membership.MembershipId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipRepository;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipEventType;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class MembershipRepositoryImpl extends AbstractGenericRepository<Membership, MembershipId> implements MembershipRepository {


    @Override
    protected String getAggregateName() {
        return "membership";
    }

    @Override
    protected Membership toAggregate(EventStream<Membership> eventStream) {
        return new Membership(eventStream);
    }

    @Override
    protected Class<? extends DomainEvent> getEventClass(EventType evenType) {
        return Stream.of(MembershipEventType.values())
                .filter(et -> et.getName().equals(evenType.getName()))
                .map(MembershipEventType::getEventClass)
                .findFirst()
                .get();
    }
}
