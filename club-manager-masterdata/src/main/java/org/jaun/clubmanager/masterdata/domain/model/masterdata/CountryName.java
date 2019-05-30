package org.jaun.clubmanager.masterdata.domain.model.masterdata;

import java.util.Locale;

public class CountryName {
    private final Locale locale;
    private final String name;

    public CountryName(Locale locale, String name) {
        this.locale = locale;
        this.name = name;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getName() {
        return name;
    }
}
