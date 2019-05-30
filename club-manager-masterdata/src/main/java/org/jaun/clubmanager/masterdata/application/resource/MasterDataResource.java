package org.jaun.clubmanager.masterdata.application.resource;

import org.jaun.clubmanager.masterdata.domain.model.masterdata.Country;
import org.jaun.clubmanager.masterdata.domain.model.masterdata.CountryService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Path("/masterdata")
public class MasterDataResource {

    @Inject
    private CountryService countryService;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("/countries")
    public Response getCountries(@QueryParam("lang") String lang) {

        Collection<Country> countries = countryService.getCountries();

        List<CountryDTO> countryDTOS = countries.stream().map(c -> toCountryDTO(c, lang)).collect(Collectors.toList());

        CountriesDTO countriesDTO = new CountriesDTO();
        countriesDTO.setCountries(countryDTOS);

        return Response.ok(countriesDTO).build();
    }

    private CountryDTO toCountryDTO(Country country, String lang) {

        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setIso2LetterCountryCode(country.getIso2LetterCountryCode());
        countryDTO.setName(country.getCountryNameWithFallback(lang));
        return countryDTO;
    }
}
