package org.jaun.clubmanager.member.domain.model.membership;

public interface MembershipRepository {
    Membership getMembership(MembershipId id);

    void store(Membership membership);
}
