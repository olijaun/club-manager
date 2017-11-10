package org.jaun.clubmanager.member.domain.model.membership;

import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;

public interface MembershipRepository {
    Membership getMembership(MembershipId id);

    void store(Membership membership);
}
