package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;

import java.util.Collection;
import java.util.stream.Collectors;

public class MemberConverter {

    public static MemberDTO toMemberDTO(Member in) {
        if (in == null) {
            return null;
        }
        MemberDTO out = new MemberDTO();
        out.setFirstName(in.getFirstName());
        out.setLastName(in.getLastName());
        return out;
    }

    public static Member toMember(MemberDTO in) {
        if (in == null) {
            return null;
        }
        return new Member(MemberId.random(MemberId::new), in.getFirstName(), in.getLastName());
    }

    public static MembersDTO toMembersDTO(Collection<Member> members) {
        MembersDTO membersDTO = new MembersDTO();
        membersDTO.setMembers(members.stream().map(MemberConverter::toMemberDTO).collect(Collectors.toList()));
        return membersDTO;
    }
}
