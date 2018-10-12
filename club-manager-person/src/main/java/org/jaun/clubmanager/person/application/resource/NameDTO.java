package org.jaun.clubmanager.person.application.resource;

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
