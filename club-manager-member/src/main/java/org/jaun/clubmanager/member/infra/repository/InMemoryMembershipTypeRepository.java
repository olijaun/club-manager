package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.member.domain.model.membership.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collection;
import java.util.Currency;

import static java.util.Arrays.asList;

@Service
public class InMemoryMembershipTypeRepository implements MembershipTypeRepository {

    public Collection<MembershipType> getAll() {

        MembershipType normal = new MembershipType(new MembershipTypeId("2"), "Normal", "Standard Mitgliedschaft");
        MembershipType goenner = new MembershipType(new MembershipTypeId("1"), "Gönner", "Gönner Mitgliedschaft");

        return asList(normal, goenner);
    }

    public MembershipType get(MembershipTypeId id) {
        return getAll().stream().filter(t -> t.getId().equals(id)).findAny().orElse(null);
    }
}
