package org.jaun.clubmanager.member.domain.model.member;

public class MemberFixture {

    public static Member newMember(MemberId memberId) {

        return new Member(memberId);
    }

    public static Member newMember() {

        MemberId memberId = new MemberId("P12345678");

        return newMember(memberId);
    }
}
