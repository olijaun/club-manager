package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipType;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeEvent;

public class MembershipTypeRepositoryImpl extends
        AbstractGenericRepository<MembershipType, MembershipTypeId, MembershipTypeEvent> implements MembershipTypeRepository {

    public MembershipTypeRepositoryImpl(EventStoreClient eventStoreClient) {
        super(eventStoreClient);
    }

    @Override
    protected String getAggregateName() {
        return "membershiptype";
    }

    @Override
    protected MembershipType toAggregate(EventStream<MembershipTypeEvent> eventStream) {
        return new MembershipType(eventStream);
    }

    @Override
    protected Class<? extends MembershipTypeEvent> getClassByName(EventType name) {
        return MembershipTypeEventMapping.of(name).getEventClass();
    }

    @Override
    protected EventType getNameByEvent(MembershipTypeEvent event) {
        return MembershipTypeEventMapping.of(event).getEventType();
    }
}