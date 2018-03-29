package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;
import java.util.Collection;

public class ContactsDTO implements Serializable {

    private Collection<ContactDTO> members;

    public Collection<ContactDTO> getMembers() {
        return members;
    }

    public void setMembers(Collection<ContactDTO> members) {
        this.members = members;
    }
}
