package org.jaun.clubmanager.member.application.resource;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.stream.Collectors;

import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriod;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;

public class ContactConverter {

    public static ContactDTO toContactDTO(Contact in) {
        if (in == null) {
            return null;
        }
        ContactDTO out = new ContactDTO();
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

    public static MembershipPeriod toMembershipPeriod(MembershipPeriodDTO in) {
        if (in == null) {
            return null;
        }

        LocalDate startDate = LocalDate.parse(in.getStartDate());
        LocalDate endDate = LocalDate.parse(in.getEndDate());

        MembershipPeriod period = new MembershipPeriod(MembershipPeriodId.random(MembershipPeriodId::new), startDate, endDate);
        period.updateMetadata(in.getName(), in.getDescription());

        return period;
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
