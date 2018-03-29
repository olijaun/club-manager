package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

public class AddressDTO extends ValueObject {

    private String street;
    private String streetNumber;
    private String zip;
    private String city;
    private String isoCountryCode;
    private String state;

    public String getStreet() {
        return street;
    }

    public AddressDTO setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public AddressDTO setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
        return this;
    }

    public String getZip() {
        return zip;
    }

    public AddressDTO setZip(String zip) {
        this.zip = zip;
        return this;
    }

    public String getCity() {
        return city;
    }

    public AddressDTO setCity(String city) {
        this.city = city;
        return this;
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    public AddressDTO setIsoCountryCode(String isoCountryCode) {
        this.isoCountryCode = isoCountryCode;
        return this;
    }

    public String getState() {
        return state;
    }

    public AddressDTO setState(String state) {
        this.state = state;
        return this;
    }
}
