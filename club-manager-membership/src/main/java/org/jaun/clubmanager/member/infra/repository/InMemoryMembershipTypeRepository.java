package org.jaun.clubmanager.member.infra.repository;

import static java.util.Arrays.asList;

import java.util.Collection;

import org.jaun.clubmanager.member.domain.model.membership.MembershipType;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeRepository;
import org.springframework.stereotype.Service;

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
