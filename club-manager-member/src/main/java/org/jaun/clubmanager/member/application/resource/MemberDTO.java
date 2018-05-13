package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class MemberDTO implements Serializable {

    private String memberId;
    private String firstName;
    private String lastName;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
