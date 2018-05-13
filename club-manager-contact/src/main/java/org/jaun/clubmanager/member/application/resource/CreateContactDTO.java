package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;
import java.time.LocalDate;

public class CreateContactDTO implements Serializable {

    private String lastNameOrCompanyName;
    private String firstName;
    private StreetAddressDTO streetAddressDTO;
    private String phoneNumber;
    private String emailAddress;
    private String sex;
    private LocalDate birthDate;
    private String contactType;

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public StreetAddressDTO getStreetAddress() {
        return streetAddressDTO;
    }

    public void setStreetAddress(StreetAddressDTO streetAddressDTO) {
        this.streetAddressDTO = streetAddressDTO;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
