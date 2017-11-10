package org.jaun.clubmanager.member.domain.model.member;

public interface MemberRepository {
    Member getMember(MemberId id);
}
