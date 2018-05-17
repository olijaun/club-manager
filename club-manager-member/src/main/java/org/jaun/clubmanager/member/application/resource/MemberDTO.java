package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class MemberDTO implements Serializable {

    private String memberId;
    private String firstName;
    private String lastNameOrCompanyName;

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

    public String getLastNameOrCompanyName() {
        return lastNameOrCompanyName;
    }

    public void setLastNameOrCompanyName(String lastNameOrCompanyName) {
        this.lastNameOrCompanyName = lastNameOrCompanyName;
    }
}
