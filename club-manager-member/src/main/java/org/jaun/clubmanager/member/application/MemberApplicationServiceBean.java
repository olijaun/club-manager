package org.jaun.clubmanager.member.application;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.collaboration.CollaboratorService;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service
public class MemberApplicationServiceBean {

    private CollaboratorService collaboratorService;

    @Autowired
    private MemberRepository memberRepository;

    public Member getMember(MemberId id) {

        Member member = new Member(id, "oliver", "jaun");

        try {
            memberRepository.save(member);
        } catch (ConcurrencyException e) {
            e.printStackTrace();
        }

        return memberRepository.get(id);
    }

    public Collection<Member> getMembers() {

        return Arrays.asList( //
                memberRepository.get(new MemberId("1")),  //
                memberRepository.get(new MemberId("2")),  //
                memberRepository.get(new MemberId("3")));
    }
}
