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
                .phone(new PhoneNumber("078 111 11 11")) //
                .address(Address.builder() //
                        .street("Somestreet 1") //
                        .city("Sometown") //
                        .zip(1234).build()).build();
    }
}
