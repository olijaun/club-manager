package org.jaun.clubmanager.member.domain.model.member;

import java.time.LocalDate;

public class MemberFixture {
    public static Member newMember() {

        MemberId memberId = new MemberId("P12345678");

        Member member = new Member(memberId);

        return member;
    }
}
