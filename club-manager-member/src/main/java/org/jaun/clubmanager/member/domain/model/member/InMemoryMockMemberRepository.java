package org.jaun.clubmanager.member.domain.model.member;

import javax.ejb.Stateless;

@Stateless
public class InMemoryMockMemberRepository implements MemberRepository {

    public Member getMember(MemberId id) {
        return Member.builder() //
                .id(id) //
                .firstName("Oliver") //
                .lastName("Jaun") //
                .emailAddress(new EmailAddress("bla@bli.ch")) //
                .phone(new PhoneNumber("078 111 11 11 11")) //
                .address(null).build();
    }
}
