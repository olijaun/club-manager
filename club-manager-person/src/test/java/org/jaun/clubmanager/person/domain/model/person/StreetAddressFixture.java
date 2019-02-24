package org.jaun.clubmanager.person.domain.model.person;

class StreetAddressFixture {

    public static StreetAddress.Builder oldStreet() {
        return StreetAddress.builder()
                .street("Oldstreet")
                .city("Oldtown")
                .houseNumber("1")
                .zip("4000")
                .country(new Country("CH"));
    }

    public static StreetAddress.Builder newStreet() {
        return StreetAddress.builder()
                .street("Newstreet")
                .city("Newtown")
                .houseNumber("42")
                .zip("3000")
                .country(new Country("CH"));
    }
}