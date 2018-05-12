package org.jaun.clubmanager.member.application.resource;

public class ContactDTO extends CreateContactDTO {

    private String contactId;

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
}
