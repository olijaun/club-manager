package org.jaun.clubmanager.masterdata.infra;

import org.jaun.clubmanager.masterdata.application.resource.DTO;

import java.util.HashMap;
import java.util.Map;

public class RestCountryDTO extends DTO {

    private String alpha2Code;
    private String name;
    private String nativeName;
    private Map<String, String> translations = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public void setNativeName(String nativeName) {
        this.nativeName = nativeName;
    }

    public String getAlpha2Code() {
        return alpha2Code;
    }

    public void setAlpha2Code(String alpha2Code) {
        this.alpha2Code = alpha2Code;
    }

    public Map<String, String> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, String> translations) {
        this.translations = translations;
    }
}
