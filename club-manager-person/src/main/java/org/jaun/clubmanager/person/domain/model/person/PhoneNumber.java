package org.jaun.clubmanager.person.domain.model.person;

import java.util.Locale;
import java.util.Objects;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class PhoneNumber extends ValueObject {

    private final String value;
    private final Locale defaultLocale;

    public PhoneNumber(String value, Locale defaultLocale) {
        this.value = Objects.requireNonNull(value);
        this.defaultLocale = defaultLocale;

        if (!PhoneNumberUtil.getInstance().isPossibleNumber(value, defaultLocale.getCountry())) {
            throw new IllegalArgumentException("invalid phone number: " + value);
        }
    }

    public String getValue() {
        return value;
    }

    public String getInternationalFormat() {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phoneNumber = null;
        try {
            phoneNumber = phoneNumberUtil.parse(value, defaultLocale.getCountry());
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("phone number cannot be formatted: " + value);
        }
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
    }
}
