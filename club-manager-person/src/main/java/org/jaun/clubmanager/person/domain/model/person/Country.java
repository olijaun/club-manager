package org.jaun.clubmanager.person.domain.model.person;

import java.util.Locale;
import java.util.MissingResourceException;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

public class Country extends ValueObject {

    private final String iso2LetterCountryCode;

    public Country(String iso2LetterCountryCode) {
        Locale locale = new Locale("", iso2LetterCountryCode);
        try {
            locale.getISO3Country(); // Locale only seams to validate the code as soon as we call this method
        } catch (MissingResourceException e) {
            // missing resource... are you serious?
            throw new IllegalArgumentException("unknown country: " + iso2LetterCountryCode);
        }
        this.iso2LetterCountryCode = locale.getCountry();
    }

    public String getIsoCountryCode() {
        return iso2LetterCountryCode;
    }
}
