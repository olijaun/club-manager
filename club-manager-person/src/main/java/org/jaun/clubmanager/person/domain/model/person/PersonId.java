package org.jaun.clubmanager.person.domain.model.person;

import java.util.regex.Pattern;

import org.jaun.clubmanager.domain.model.commons.Id;

public class PersonId extends Id {

    private static final Pattern PATTERN = Pattern.compile("P\\d{8}");

    public PersonId(String value) {
        super(value);
        if(!isValid(value)) {
            throw new IllegalArgumentException("invalid person id: " + value);
        }
    }

    public static boolean isValid(String value) {
        return PATTERN.matcher(value).matches();
    }

    public int asInt() {
        return Integer.valueOf(getValue().substring(1, getValue().length()));
    }
}
