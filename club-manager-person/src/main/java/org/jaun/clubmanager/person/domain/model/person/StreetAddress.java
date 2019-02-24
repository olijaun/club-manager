package org.jaun.clubmanager.person.domain.model.person;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

public class StreetAddress extends ValueObject {

    private final String street;
    private final String houseNumber;
    private final String zip;
    private final String city;
    private final Country country;
    private final String state;

    private StreetAddress(Builder builder) {
        this.street = requireNonNull(builder.street);
        this.houseNumber = builder.houseNumber;
        this.zip = requireNonNull(builder.zip);
        this.city = requireNonNull(builder.city);
        this.state = builder.state;
        this.country = requireNonNull(builder.country);
    }

    public String getStreet() {
        return street;
    }

    public Optional<String> getHouseNumber() {
        return Optional.ofNullable(houseNumber);
    }

    public String getZip() {
        return zip;
    }

    public String getCity() {
        return city;
    }

    public Optional<String> getState() {
        return Optional.ofNullable(state);
    }

    public Country getCountry() {
        return country;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String street;
        private String houseNumber;
        private String zip;
        private String city;
        private String state;
        private Country country;

        public StreetAddress build() {
            return new StreetAddress(this);
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

        public Builder houseNumber(String houseNumber) {
            this.houseNumber = houseNumber;
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
