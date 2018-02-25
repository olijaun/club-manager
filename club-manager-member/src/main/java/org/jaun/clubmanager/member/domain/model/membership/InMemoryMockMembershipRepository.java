package org.jaun.clubmanager.member.domain.model.membership;

public class InMemoryMockMembershipRepository implements MembershipRepository {

    public Membership getMembership(MembershipId id) {
        return new Membership(id, 2017, Membership.Type.STANDARD);
    }

    public void store(Membership membership) {

    }
}
