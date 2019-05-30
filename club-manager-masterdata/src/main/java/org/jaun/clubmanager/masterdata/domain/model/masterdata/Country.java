package org.jaun.clubmanager.masterdata.domain.model.masterdata;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class Country {

    private final String iso2LetterCountryCode;
    private final String nativeName;
    private final Map<String, CountryName> countryMap;

    public Country(String iso2LetterCountryCode, String nativeName, Collection<CountryName> countryNames) {
        this.iso2LetterCountryCode = iso2LetterCountryCode;
        this.nativeName = nativeName;
        countryMap = countryNames.stream().collect(
                toMap(c -> c.getLocale().getLanguage(), Function.identity(), (x, y) -> x));
    }

    public String getIso2LetterCountryCode() {
        return iso2LetterCountryCode;
    }

    public String getCountryNameWithFallback(String languageCode) {

        CountryName countryName = countryMap.get(languageCode);

        if(countryName == null) {
            return nativeName;
        }

        return countryName.getName();
    }
}
