package org.jaun.clubmanager.person.application.resource;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class CreatePersonDTO extends DTO {

    private String type;
    @Valid
    @NotNull
    private BasicDataDTO basicData;
    @Valid
    private StreetAddressDTO streetAddress;
    @Valid
    private ContactDataDTO contactData;

    public String getType() {
        return type;
    }

    public CreatePersonDTO setType(String type) {
        this.type = type;
        return this;
    }

    public BasicDataDTO getBasicData() {
        return basicData;
    }

    public CreatePersonDTO setBasicData(BasicDataDTO basicData) {
        this.basicData = basicData;
        return this;
    }

    public StreetAddressDTO getStreetAddress() {
        return streetAddress;
    }

    public CreatePersonDTO setStreetAddress(StreetAddressDTO streetAddress) {
        this.streetAddress = streetAddress;
        return this;
    }

    public ContactDataDTO getContactData() {
        return contactData;
    }

    public CreatePersonDTO setContactData(ContactDataDTO contactData) {
        this.contactData = contactData;
        return this;
    }
}
