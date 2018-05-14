package org.jaun.clubmanager.member.infra.service;

import org.jaun.clubmanager.member.domain.model.contact.ContactId;

public class ContactDTO {
    private ContactId contactId;
    private String firstName;
    private String lastNameOrCompanyName;

    public ContactId getContactId() {
        return contactId;
    }

    public void setContactId(ContactId contactId) {
        this.contactId = contactId;
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
