package org.jaun.clubmanager.contact.application.resource;

import java.io.Serializable;

public class NameDTO extends DTO {
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
