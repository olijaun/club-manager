package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.MemberRepository;
import org.jaun.clubmanager.member.domain.model.member.event.MemberEvent;
import org.springframework.stereotype.Service;

@Service
public class MemberRepositoryImpl extends AbstractGenericRepository<Member, MemberId, MemberEvent> implements MemberRepository {

    @Override
    protected String getAggregateName() {
        return "member";
    }

    @Override
    protected Member toAggregate(EventStream<MemberEvent> eventStream) {
        return new Member(eventStream);
    }

    @Override
    protected Class<? extends MemberEvent> getClassByName(String name) {
        return MemberEventMapping.of(name).getEventClass();
    }

    @Override
    protected String getNameByEvent(MemberEvent event) {
        return MemberEventMapping.of(event).getEventType();
    }
}