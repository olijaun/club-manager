package org.jaun.clubmanager.member.domain.model;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

import static java.util.Objects.requireNonNull;

/**
 * Currently only supports simple "swiss" addresses. The country is assumed to be switzerland. This will be extended in the future.
 */
public class Address extends ValueObject {

    private final String street;
    private final int plz;
    private final String city;

    public Address(String street, int plz, String city) {
        this.street = requireNonNull(street);
        this.plz = requireNonNull(plz);
        this.city = requireNonNull(city);
    }

    public String getStreet() {
        return street;
    }

    public int getPlz() {
        return plz;
    }

    public String getCity() {
        return city;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String street;
        private int plz;
        private String city;

        public Address build() {
            return new Address(street, plz, city);
        }

        public Builder street(String street) {
            this.street = street;
            return this;
        }

        public Builder plz(int plz) {
            this.plz = plz;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }
    }
}
