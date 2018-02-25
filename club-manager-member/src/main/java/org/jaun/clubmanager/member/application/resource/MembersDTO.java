package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;
import java.util.Collection;

public class MembersDTO implements Serializable {

    private Collection<MemberDTO> members;

    public Collection<MemberDTO> getMembers() {
        return members;
    }

    public void setMembers(Collection<MemberDTO> members) {
        this.members = members;
    }
}
