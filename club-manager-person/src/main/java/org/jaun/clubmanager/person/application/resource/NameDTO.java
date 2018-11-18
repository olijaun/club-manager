package org.jaun.clubmanager.person.application.resource;

import javax.validation.constraints.NotNull;

public class NameDTO extends DTO {
    @NotNull
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
