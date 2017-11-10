package org.jaun.clubmanager.member.domain.model.member;

import org.jaun.clubmanager.domain.model.commons.Entity;

import static java.util.Objects.requireNonNull;

public class Member extends Entity<MemberId> {

    private final MemberId id;
    private final String firstName;
    private final String lastName;
    private final Address address;
    private final PhoneNumber phone;
    private final EmailAddress emailAddress;

    public Member(MemberId id, String firstName, String lastName, Address address, PhoneNumber phone, EmailAddress emailAddress) {
        this.id = requireNonNull(id);
        this.firstName = requireNonNull(firstName);
        this.lastName = requireNonNull(lastName);
        this.address = requireNonNull(address);
        this.phone = phone;
        this.emailAddress = emailAddress;
    }

    public MemberId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Address getAddress() {
        return address;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MemberId id;
        private String firstName;
        private String lastName;
        private Address address;
        private PhoneNumber phone;
        private EmailAddress emailAddress;

        public Member build() {
            return new Member(id, firstName, lastName, address, phone, emailAddress);
        }

        public Builder id(MemberId id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public Builder phone(PhoneNumber phone) {
            this.phone = phone;
            return this;
        }

        public Builder emailAddress(EmailAddress emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }
    }
}
