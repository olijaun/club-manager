package org.jaun.clubmanager.masterdata.infra;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jaun.clubmanager.masterdata.domain.model.masterdata.Country;
import org.jaun.clubmanager.masterdata.domain.model.masterdata.CountryName;
import org.jaun.clubmanager.masterdata.domain.model.masterdata.CountryService;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class RestCountriesService implements CountryService {

    private final WebTarget target;

    public RestCountriesService(WebTarget target) {
        this.target = target;
    }

    @Override
    public Collection<Country> getCountries() {


        List<RestCountryDTO> restCountryDTOS;

        try {
            restCountryDTOS = target.path("21313123/rest/v2/all").queryParam("fields", "alpha2Code;nativeName;name;translations").request().get(new GenericType<List<RestCountryDTO>>() {
            });

        } catch (Exception e) {

            // fallback

            URL url = getClass().getResource("/countries.json");

            ObjectMapper mapper = new ObjectMapper();
            try {
                restCountryDTOS = mapper.readValue(url, new TypeReference<List<RestCountryDTO>>() {
                });
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        return restCountryDTOS.stream().map(rc -> new Country(rc.getAlpha2Code(), rc.getNativeName(), toCountryName(rc))).collect(Collectors.toList());
    }

    private Collection<CountryName> toCountryName(RestCountryDTO restCountryDTO) {
        List<CountryName> countryNames = restCountryDTO.getTranslations().entrySet().stream().map(e -> new CountryName(new Locale(e.getKey()), e.getValue())).collect(Collectors.toList());
        countryNames.add(new CountryName(Locale.ENGLISH, restCountryDTO.getName()));
        return countryNames;
    }
}
