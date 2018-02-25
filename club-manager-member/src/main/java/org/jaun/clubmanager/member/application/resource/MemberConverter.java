package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.member.domain.model.member.Member;

import java.util.Collection;
import java.util.stream.Collectors;

public class MemberConverter {

    public static MemberDTO toMemberDTO(Member in) {
        MemberDTO out = new MemberDTO();
        out.setId(in.getId().getValue());
        out.setFirstName(in.getFirstName());
        out.setLastName(in.getLastName());
        return out;
    }

    public static MembersDTO toMembersDTO(Collection<Member> members) {
        MembersDTO membersDTO = new MembersDTO();
        membersDTO.setMembers(members.stream().map(MemberConverter::toMemberDTO).collect(Collectors.toList()));
        return membersDTO;
    }
}
