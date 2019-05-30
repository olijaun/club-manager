package org.jaun.clubmanager.masterdata.domain.model.masterdata;

import org.jaun.clubmanager.domain.model.commons.Entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class Country extends Entity<Iso2LetterCountryCode> {

    private final Iso2LetterCountryCode iso2LetterCountryCode;
    private final String nativeName;
    private final Map<String, CountryName> countryMap;

    public Country(Iso2LetterCountryCode iso2LetterCountryCode, String nativeName, Collection<CountryName> countryNames) {
        this.iso2LetterCountryCode = iso2LetterCountryCode;
        this.nativeName = nativeName;
        countryMap = countryNames.stream().collect(
                toMap(c -> c.getLocale().getLanguage(), Function.identity(), (x, y) -> x));
    }

    public String getCountryNameWithFallback(String languageCode) {

        CountryName countryName = countryMap.get(languageCode);

        if (countryName == null) {
            return nativeName;
        }

        return countryName.getName();
    }

    @Override
    public Iso2LetterCountryCode getId() {
        return iso2LetterCountryCode;
    }
}
