package org.jaun.clubmanager.member.application;

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
        return memberRepository.getMember(id);
    }

    public Collection<Member> getMembers() {

        return Arrays.asList( //
                memberRepository.getMember(new MemberId("1")),  //
                memberRepository.getMember(new MemberId("2")),  //
                memberRepository.getMember(new MemberId("3")));
    }
}
