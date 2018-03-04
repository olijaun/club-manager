package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriod;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.stream.Collectors;

public class ContactConverter {

    public static MemberDTO toContactDTO(Contact in) {
        if (in == null) {
            return null;
        }
        MemberDTO out = new MemberDTO();
        out.setFirstName(in.getFirstName());
        out.setLastName(in.getLastName());
        return out;
    }

    public static Contact toMember(MemberDTO in) {
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

        Period p = Period.between(startDate, endDate);

        MembershipPeriod period = new MembershipPeriod(MembershipPeriodId.random(MembershipPeriodId::new), p);
        period.updateMetadata(in.getName(), in.getDescription());

        return period;
    }

    public static MembersDTO toMembersDTO(Collection<Contact> members) {
        MembersDTO membersDTO = new MembersDTO();
        membersDTO.setMembers(members.stream().map(ContactConverter::toContactDTO).collect(Collectors.toList()));
        return membersDTO;
    }
}
