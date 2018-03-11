package org.jaun.clubmanager.member.domain.model.contact;

import static java.util.Objects.requireNonNull;

import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class EmailAddress {

    private static Pattern TLD_PATTERN = Pattern.compile("[^@]+@[^@]+\\.[^@]+");
    private String value;

    public EmailAddress(String value) {

        this.value = requireNonNull(value);

        try {
            new InternetAddress(value);
        } catch (AddressException e) {
            throw new IllegalArgumentException("illegal email address: " + value, e);
        }

        // make sure it is not a local email address
        if (!TLD_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("Email must contain a TLD: " + value);
        }
    }

    public String getValue() {
        return value;
    }
}
