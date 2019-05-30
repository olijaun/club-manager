package org.jaun.clubmanager.masterdata.domain.model.masterdata;

import org.jaun.clubmanager.domain.model.commons.Id;

import static com.google.common.base.Preconditions.checkArgument;

public class Iso2LetterCountryCode extends Id {

    public Iso2LetterCountryCode(String value) {
        super(value);

        checkArgument(value.matches("[A-Z]{2}"), "invalid iso 2 letter country code");
    }
}
