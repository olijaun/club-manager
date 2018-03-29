package org.jaun.clubmanager.member.application.resource;

import java.util.Collection;
import java.util.Currency;
import java.util.stream.Collectors;

import org.jaun.clubmanager.member.domain.model.contact.Address;
import org.jaun.clubmanager.member.domain.model.contact.Company;
import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.EmailAddress;
import org.jaun.clubmanager.member.domain.model.contact.Person;

public class ContactConverter {

    public static PersonDTO toPersonDTO(Person in) {
        if (in == null) {
            return null;
        }
        PersonDTO out = new PersonDTO();
        out.setContactId(in.getId().getValue());
        out.setFirstName(in.getName().getFirstName().orElse(null));
        out.setMiddleName(in.getName().getMiddleName().orElse(null));
        out.setLastName(in.getName().getLastName());
        out.setAddressDTO(toAddressDTO(in.getAddress()));
        out.setBirthDate(in.getBirthDate().orElse(null));
        out.setSex(in.getSex().orElse(null).name()); // TODO: should be mapped
        out.setEmailAddress(in.getEmailAddress().map(EmailAddress::getValue).orElse(null));
        return out;
    }

    private static AddressDTO toAddressDTO(Address address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setCity(address.getCity());
        addressDTO.setIsoCountryCode(address.getCountry().getIsoCountryCode());
        addressDTO.setStreet(address.getStreet());
        addressDTO.setStreetNumber(address.getStreetNumber());
        addressDTO.setZip(address.getZip());
        addressDTO.setState(address.getState());
        return addressDTO;
    }

    public static Person toPerson(PersonDTO in) {
        if (in == null) {
            return null;
        }
        return new Person(ContactId.random(ContactId::new), in.getFirstName(), in.getLastName());
    }

    public static Company toCompany(ContactDTO in) {
        if (in == null) {
            return null;
        }
        return new Contact(ContactId.random(ContactId::new), in.getFirstName(), in.getLastName());
    }

    public static ContactsDTO toMembersDTO(Collection<Contact> members) {
        ContactsDTO contactsDTO = new ContactsDTO();
        contactsDTO.setMembers(members.stream().map(ContactConverter::toContactDTO).collect(Collectors.toList()));
        return contactsDTO;
    }

    public static Currency toCurrency(String currencyCode) {
        return Currency.getInstance(currencyCode);
    }
}
