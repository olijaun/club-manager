package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class MemberDTO implements Serializable {
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public MemberDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public MemberDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
