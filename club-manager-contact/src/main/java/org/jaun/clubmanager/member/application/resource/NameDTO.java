package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class NameDTO implements Serializable {
    private String lastNameOrCompanyName;
    private String firstName;

    public String getLastNameOrCompanyName() {
        return lastNameOrCompanyName;
    }

    public void setLastNameOrCompanyName(String lastNameOrCompanyName) {
        this.lastNameOrCompanyName = lastNameOrCompanyName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
