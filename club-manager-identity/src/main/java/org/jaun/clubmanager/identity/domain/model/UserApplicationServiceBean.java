package org.jaun.clubmanager.identity.domain.model;

public class UserApplicationServiceBean {

    private UserRepository userRepository;

    public User getUser(UserId id) {
        return userRepository.getUser(id);
    }
}
