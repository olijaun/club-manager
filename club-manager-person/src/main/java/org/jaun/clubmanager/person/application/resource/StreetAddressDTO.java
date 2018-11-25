package org.jaun.clubmanager.person.application.resource;

public class StreetAddressDTO extends DTO {

    private String street;
    private String houseNumber;
    private String zip;
    private String city;
    private String isoCountryCode;
    private String state;

    public String getStreet() {
        return street;
    }

    public StreetAddressDTO setStreet(String street) {
        this.street = street;
        return this;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public StreetAddressDTO setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
        return this;
    }

    public String getZip() {
        return zip;
    }

    public StreetAddressDTO setZip(String zip) {
        this.zip = zip;
        return this;
    }

    public String getCity() {
        return city;
    }

    public StreetAddressDTO setCity(String city) {
        this.city = city;
        return this;
    }

    public String getIsoCountryCode() {
        return isoCountryCode;
    }

    public StreetAddressDTO setIsoCountryCode(String isoCountryCode) {
        this.isoCountryCode = isoCountryCode;
        return this;
    }

    public String getState() {
        return state;
    }

    public StreetAddressDTO setState(String state) {
        this.state = state;
        return this;
    }
}
