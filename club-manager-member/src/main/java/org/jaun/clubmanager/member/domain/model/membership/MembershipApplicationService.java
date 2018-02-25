package org.jaun.clubmanager.member.domain.model.membership;

import java.time.LocalDate;

public class MembershipApplicationService {

    private MembershipRepository membershipRepository;

    public void recordPayment(MembershipId membershipId, LocalDate dateOfPayment) {
        Membership membership = membershipRepository.getMembership(membershipId);
        membership.recordPayment(dateOfPayment);
        membershipRepository.store(membership);
    }
}
