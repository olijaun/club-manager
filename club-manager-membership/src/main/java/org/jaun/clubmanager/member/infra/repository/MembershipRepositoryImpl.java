package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.membership.Membership;
import org.jaun.clubmanager.member.domain.model.membership.MembershipId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipRepository;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipEvent;
import org.springframework.stereotype.Service;

@Service
public class MembershipRepositoryImpl extends AbstractGenericRepository<Membership, MembershipId, MembershipEvent> implements
        MembershipRepository {

    @Override
    protected String getAggregateName() {
        return "membership";
    }

    @Override
    protected Membership toAggregate(EventStream<MembershipEvent> eventStream) {
        return new Membership(eventStream);
    }

    @Override
    protected Class<? extends MembershipEvent> getClassByName(String name) {
        return MembershipEventMapping.of(name).getEventClass();
    }

    @Override
    protected String getNameByEvent(MembershipEvent event) {
        return MembershipEventMapping.of(event).getEventType();
    }
}