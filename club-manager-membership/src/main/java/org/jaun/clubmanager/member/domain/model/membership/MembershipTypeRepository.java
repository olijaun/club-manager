package org.jaun.clubmanager.member.domain.model.membership;

import java.util.Collection;

public interface MembershipTypeRepository {
    MembershipType get(MembershipTypeId id);

    Collection<MembershipType> getAll();
}
