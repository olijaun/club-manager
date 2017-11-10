package org.jaun.clubmanager.domain.model;

import javax.ejb.*;

@Stateless
@TransactionManagement(TransactionManagementType.CONTAINER)
public class UserApplicationServiceBean {

    @EJB
    private UserRepository userRepository;

    @TransactionAttribute(TransactionAttributeType.SUPPORTS)
    public User getUser(UserId id) {
        return userRepository.getUser(id);
    }
}
