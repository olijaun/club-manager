package org.jaun.clubmanager.member.application.resource;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.stream.Collectors;

import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.ContactType;
import org.jaun.clubmanager.member.domain.model.contact.Country;
import org.jaun.clubmanager.member.domain.model.contact.EmailAddress;
import org.jaun.clubmanager.member.domain.model.contact.Name;
import org.jaun.clubmanager.member.domain.model.contact.PhoneNumber;
import org.jaun.clubmanager.member.domain.model.contact.Sex;
import org.jaun.clubmanager.member.domain.model.contact.StreetAddress;

public class ContactConverter {

    public static ContactDTO toContactDTO(Contact in) {
        if (in == null) {
            return null;
        }
        ContactDTO out = new ContactDTO();
        out.setContactId(in.getId().getValue());
        out.setName(toNameDTO(in.getName()));
        out.setStreetAddress(toAddressDTO(in.getStreetAddress()));
        out.setBirthDate(in.getBirthDate().orElse(null));
        out.setSex(in.getSex().map(ContactConverter::toSexAsString).orElse(null));
        out.setEmailAddress(in.getEmailAddress().map(EmailAddress::getValue).orElse(null));
        return out;
    }

    public static NameDTO toNameDTO(Name name) {
        NameDTO nameDTO = new NameDTO();
        nameDTO.setFirstName(name.getFirstName().orElse(null));
        nameDTO.setLastNameOrCompanyName(name.getLastNameOrCompanyName());
        return nameDTO;
    }

    public static String toSexAsString(Sex sex) {
        switch (sex) {
            case MALE:
                return "MALE";
            case FEMALE:
                return "FEMALE";
            default:
                throw new IllegalArgumentException("unknown sex: " + sex);
        }
    }

    public static Sex toSex(String sex) {
        switch (sex) {
            case "MALE":
                return Sex.MALE;
            case "FEMALE":
                return Sex.FEMALE;
            default:
                throw new IllegalArgumentException("invalid sex: " + sex);
        }
    }

    public static StreetAddressDTO toAddressDTO(StreetAddress streetAddress) {
        if (streetAddress == null) {
            return null;
        }
        StreetAddressDTO streetAddressDTO = new StreetAddressDTO();
        streetAddressDTO.setCity(streetAddress.getCity());
        streetAddressDTO.setIsoCountryCode(streetAddress.getCountry().getIsoCountryCode());
        streetAddressDTO.setStreet(streetAddress.getStreet());
        streetAddressDTO.setStreetNumber(streetAddress.getStreetNumber());
        streetAddressDTO.setZip(streetAddress.getZip());
        streetAddressDTO.setState(streetAddress.getState());
        return streetAddressDTO;
    }

    public static StreetAddress toStreetAddress(StreetAddressDTO in) {
        if (in == null) {
            return null;
        }
        return StreetAddress.builder()
                .city(in.getCity())
                .country(new Country(in.getIsoCountryCode()))
                .street(in.getStreet())
                .streetNumber(in.getStreetNumber())
                .zip(in.getZip())
                .state(in.getState())
                .build();
    }

    public static Contact toContact(ContactId contractId, CreateContactDTO in) {
        if (in == null) {
            return null;
        }
        ContactType contactType = toContactType(in);

        Contact contact = new Contact(contractId, contactType, toName(in));
        contact.changeStreetAddress(toStreetAddress(in.getStreetAddress()));
        contact.changeEmailAddress(in.getEmailAddress() == null ? null : new EmailAddress(in.getEmailAddress()));
        contact.changePhoneNumber(in.getPhoneNumber() == null ? null : new PhoneNumber(in.getPhoneNumber()));

        if (ContactType.PERSON.equals(contactType)) {
            contact.changeBirthdate(in.getBirthDate());
            contact.changeSex(in.getSex() == null ? null : Sex.valueOf(in.getSex()));
        }

        return contact;
    }

    public static Name toName(CreateContactDTO in) {
        return toName(in.getName());
    }

    public static Name toName(NameDTO in) {
        return new Name(in.getLastNameOrCompanyName(), in.getFirstName());
    }

    public static ContactType toContactType(CreateContactDTO contactDTO) {
        switch (contactDTO.getContactType()) {
            case "PERSON":
                return ContactType.PERSON;
            case "COMPANY":
                return ContactType.COMPANY;
            default:
                throw new IllegalArgumentException("unknown contact type");
        }
    }

    public static ContactsDTO toContactsDTO(Collection<Contact> contacts) {
        ContactsDTO contactsDTO = new ContactsDTO();
        contactsDTO.setContacts(contacts.stream().map(ContactConverter::toContactDTO).collect(Collectors.toList()));
        return contactsDTO;
    }

    public static Currency toCurrency(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }

    public static LocalDate toLocalDate(String birthDateAsString) {

        return LocalDate.parse(birthDateAsString);
    }

    public static PhoneNumber toPhoneNumber(String phoneNumberAsString) {
        return new PhoneNumber(phoneNumberAsString);
    }

    public static EmailAddress toEmailAddress(String emailAddressAsString) {
        return new EmailAddress(emailAddressAsString);
    }
}
