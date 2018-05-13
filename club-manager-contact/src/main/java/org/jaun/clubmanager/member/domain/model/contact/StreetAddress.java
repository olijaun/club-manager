package org.jaun.clubmanager.member.domain.model.contact;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

public class StreetAddress extends ValueObject {

    private final String street;
    private final String streetNumber;
    private final String zip;
    private final String city;
    private final Country country;
    private final String state;

    public StreetAddress(String street, String streetNumber, String zip, String city, String state, Country country) {
        this.street = requireNonNull(street);
        this.streetNumber = streetNumber;
        this.zip = requireNonNull(zip);
        this.city = requireNonNull(city);
        this.state = state;
        this.country = requireNonNull(country);
    }

    public String getStreet() {
        return street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public Country getCountry() {
        return country;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String street;
        private String streetNumber;
        private String zip;
        private String city;
        private String state;
        private Country country;

        public StreetAddress build() {
            return new StreetAddress(street, streetNumber, zip, state, city, country);
        }

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder zip(String zip) {
            this.zip = zip;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder streetNumber(String streetNumber) {
            this.streetNumber = streetNumber;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder country(Country country) {
            this.country = country;
            return this;
        }
    }
}
