package org.jaun.clubmanager.member.domain.model.member;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.Locale;
import java.util.Objects;

public class PhoneNumber {

    private final String value;

    public PhoneNumber(String value) {

        this.value = Objects.requireNonNull(value);

        if (!PhoneNumberUtil.getInstance().isPossibleNumber(value, Locale.getDefault().getCountry())) {
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
            phoneNumber = phoneNumberUtil.parse(value, Locale.getDefault().getCountry());
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("phone number cannot be formatted: " + value);
        }
        return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
    }
}