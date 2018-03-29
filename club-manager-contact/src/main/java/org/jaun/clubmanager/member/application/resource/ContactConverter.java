package org.jaun.clubmanager.member.application.resource;

import java.util.Collection;
import java.util.Currency;
import java.util.stream.Collectors;

import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;

public class ContactConverter {

    public static ContactDTO toContactDTO(Contact in) {
        if (in == null) {
            return null;
        }
        ContactDTO out = new ContactDTO();
        out.setContactId(in.getId().getValue());
        out.setFirstName(in.getFirstName());
        out.setLastName(in.getLastName());
        return out;
    }

    public static Contact toContact(ContactDTO in) {
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
