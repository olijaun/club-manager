package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipType;

public class MembershipTypeConverter {

    public static MembershipTypeDTO toMembershipTypeDTO(MembershipType membershipType) {
        MembershipTypeDTO membershipTypeDTO = new MembershipTypeDTO();
        membershipTypeDTO.setId(membershipType.getId().getValue());
        membershipTypeDTO.setName(membershipType.getName());
        membershipTypeDTO.setDescription(membershipType.getDescription().orElse(null));
        return membershipTypeDTO;
    }
}
