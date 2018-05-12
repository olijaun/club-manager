package org.jaun.clubmanager.member.application.resource;

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
        out.setFirstName(in.getName().getFirstName().orElse(null));
        out.setLastNameOrCompanyName(in.getName().getLastNameOrCompanyName());
        out.setStreetAddressDTO(toAddressDTO(in.getStreetAddress()));
        out.setBirthDate(in.getBirthDate().orElse(null));
        out.setSex(in.getSex().map(Sex::name).orElse(null)); // TODO: should be mapped
        out.setEmailAddress(in.getEmailAddress().map(EmailAddress::getValue).orElse(null));
        return out;
    }

    private static StreetAddressDTO toAddressDTO(StreetAddress streetAddress) {
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

    private static StreetAddress toStreetAddress(StreetAddressDTO in) {
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

    public static Contact toContact(ContactDTO in) {
        if (in == null) {
            return null;
        }
        ContactType contactType = toContactType(in);

        Contact contact = new Contact(new ContactId(in.getContactId()), contactType, toName(in));
        contact.changeStreetAddress(toStreetAddress(in.getStreetAddressDTO()));
        contact.changeEmailAddress(new EmailAddress(in.getEmailAddress()));
        contact.changePhoneNumber(new PhoneNumber(in.getPhoneNumber()));

        if (ContactType.PERSON.equals(contactType)) {
            contact.changeBirthdate(in.getBirthDate());
            contact.changeSex(Sex.valueOf(in.getSex()));
        }

        return contact;
    }

    public static Name toName(ContactDTO in) {
        return new Name(in.getLastNameOrCompanyName(), in.getFirstName());
    }

    public static ContactType toContactType(ContactDTO contactDTO) {
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
}
