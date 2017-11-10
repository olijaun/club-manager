package org.jaun.clubmanager.member.domain.model.membership;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDate;

@Stateless
public class MembershipApplicationService {

    @EJB
    private MembershipRepository membershipRepository;

    public void recordPayment(MembershipId membershipId, LocalDate dateOfPayment) {
        Membership membership = membershipRepository.getMembership(membershipId);
        membership.recordPayment(dateOfPayment);
        membershipRepository.store(membership);
    }
}
