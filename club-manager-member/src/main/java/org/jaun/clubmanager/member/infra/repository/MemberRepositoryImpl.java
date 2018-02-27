package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.domain.model.commons.EventType;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.MemberRepository;
import org.jaun.clubmanager.member.domain.model.member.event.MemberEventType;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

@Service
public class MemberRepositoryImpl extends AbstractGenericRepository<Member, MemberId> implements MemberRepository {

    @Override
    protected String getAggregateName() {
        return "member";
    }

    @Override
    protected Member toAggregate(EventStream<Member> eventStream) {
        return new Member(eventStream);
    }

    @Override
    protected Class<? extends DomainEvent> getEventClass(EventType evenType) {
        return Stream.of(MemberEventType.values())
                .filter(et -> et.getName().equals(evenType.getName()))
                .map(MemberEventType::getEventClass)
                .findFirst()
                .get();
    }
}
