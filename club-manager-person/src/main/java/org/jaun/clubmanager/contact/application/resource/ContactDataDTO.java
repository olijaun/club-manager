package org.jaun.clubmanager.contact.application.resource;

public class ContactDataDTO extends DTO {
    private String phoneNumber;
    private String emailAddress;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ContactDataDTO setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public ContactDataDTO setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }
}
