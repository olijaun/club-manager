package org.jaun.clubmanager.member.domain.model.member;

import javax.ejb.*;
import java.util.Arrays;
import java.util.Collection;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class MemberApplicationServiceBean {

    @EJB
    private MemberRepository userRepository;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Member getMember(MemberId id) {
        return userRepository.getMember(id);
    }

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public Collection<Member> getMembers() {
        return Arrays.asList( //
                userRepository.getMember(new MemberId("1")),  //
                userRepository.getMember(new MemberId("2")),  //
                userRepository.getMember(new MemberId("3")));
    }
}
