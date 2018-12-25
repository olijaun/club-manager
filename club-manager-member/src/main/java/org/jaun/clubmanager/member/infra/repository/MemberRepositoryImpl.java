package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.MemberRepository;
import org.jaun.clubmanager.member.domain.model.member.event.MemberEvent;

public class MemberRepositoryImpl extends AbstractGenericRepository<Member, MemberId, MemberEvent> implements MemberRepository {

    public MemberRepositoryImpl(EventStoreClient eventStoreClient) {
        super(eventStoreClient);
    }

    @Override
    protected String getAggregateName() {
        return "member";
    }

    @Override
    protected Member toAggregate(EventStream<MemberEvent> eventStream) {
        return new Member(eventStream);
    }

    @Override
    protected Class<? extends MemberEvent> getClassByName(EventType name) {
        return MemberEventMapping.of(name).getEventClass();
    }

    @Override
    protected EventType getNameByEvent(MemberEvent event) {
        return MemberEventMapping.of(event).getEventType();
    }
}