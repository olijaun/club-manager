package org.jaun.clubmanager.domain.model;

import javax.ejb.Stateless;

@Stateless
public class InMemoryMockUserRepository implements UserRepository {

    public User getUser(UserId id) {
        return new User(new UserId("userA"), "Mr. A");
    }
}
