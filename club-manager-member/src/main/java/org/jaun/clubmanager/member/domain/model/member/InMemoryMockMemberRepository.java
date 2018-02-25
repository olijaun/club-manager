package org.jaun.clubmanager.member.domain.model.member;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
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
