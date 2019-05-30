package org.jaun.clubmanager.masterdata.application.resource;

public class CountryDTO extends DTO {

    private String iso2LetterCountryCode;
    private String name;

    public String getIso2LetterCountryCode() {
        return iso2LetterCountryCode;
    }

    public void setIso2LetterCountryCode(String iso2LetterCountryCode) {
        this.iso2LetterCountryCode = iso2LetterCountryCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
