package org.jaun.clubmanager.member.domain.model;

public interface MemberRepository {
    Member getMember(MemberId id);
}
