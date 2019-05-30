package org.jaun.clubmanager.masterdata.application.resource;

import java.util.Collection;

public class CountriesDTO extends DTO {
    private Collection<CountryDTO> countries;

    public Collection<CountryDTO> getCountries() {
        return countries;
    }

    public void setCountries(Collection<CountryDTO> countries) {
        this.countries = countries;
    }
}
