package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipType;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeEvent;
import org.springframework.stereotype.Service;

@Service
public class MembershipTypeRepositoryImpl extends
        AbstractGenericRepository<MembershipType, MembershipTypeId, MembershipTypeEvent> implements MembershipTypeRepository {

    @Override
    protected String getAggregateName() {
        return "membershiptype";
    }

    @Override
    protected MembershipType toAggregate(EventStream<MembershipTypeEvent> eventStream) {
        return new MembershipType(eventStream);
    }

    @Override
    protected Class<? extends MembershipTypeEvent> getClassByName(String name) {
        return MembershipTypeEventMapping.of(name).getEventClass();
    }

    @Override
    protected String getNameByEvent(MembershipTypeEvent event) {
        return MembershipTypeEventMapping.of(event).getEventType();
    }
}