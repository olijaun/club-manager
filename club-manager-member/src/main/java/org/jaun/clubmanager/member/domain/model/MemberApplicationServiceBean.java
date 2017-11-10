package org.jaun.clubmanager.member.domain.model;

import javax.ejb.*;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class MemberApplicationServiceBean {

    @EJB
    private MemberRepository userRepository;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Member getUser(MemberId id) {
        return userRepository.getMember(id);
    }
}
