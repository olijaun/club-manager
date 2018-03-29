package org.jaun.clubmanager.member.domain.model.contact;

import java.util.Locale;

public class Country {

    private final Locale locale;

    public Country(String isoCountryCode) {
        locale = new Locale("", isoCountryCode);
    }

    public String getIsoCountryCode() {
        return locale.getISO3Country();
    }
}
