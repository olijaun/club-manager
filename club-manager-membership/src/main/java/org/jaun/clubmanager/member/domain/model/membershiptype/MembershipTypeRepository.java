package org.jaun.clubmanager.member.domain.model.membershiptype;

import java.util.Collection;

public interface MembershipTypeRepository {
    MembershipType get(MembershipTypeId id);

    Collection<MembershipType> getAll();
}
