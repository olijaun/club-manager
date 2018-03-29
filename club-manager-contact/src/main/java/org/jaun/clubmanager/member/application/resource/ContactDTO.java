package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class ContactDTO implements Serializable {

    private String contactId;
    private String firstName;
    private String lastName;

    public String getContactId() {
        return contactId;
    }

    public ContactDTO setContactId(String memberId) {
        this.contactId = memberId;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public ContactDTO setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public ContactDTO setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
}
