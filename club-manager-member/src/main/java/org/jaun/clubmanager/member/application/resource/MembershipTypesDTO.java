package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MembershipTypesDTO implements Serializable {
    private ArrayList<MembershipTypeDTO> membershipTypes = new ArrayList<>();

    public List<MembershipTypeDTO> getMembershipTypes() {
        return membershipTypes;
    }

    public void setMembershipTypes(Collection<MembershipTypeDTO> membershipTypesDTOS) {
        if (membershipTypesDTOS != null) {
            this.membershipTypes = new ArrayList<>(membershipTypesDTOS);
        }
    }
}
