package org.jaun.clubmanager.identity.domain.model;

import javax.ejb.Stateless;

@Stateless
public class InMemoryMockUserRepository implements UserRepository {

    public User getUser(UserId id) {
        return new User(new UserId(id.getValue()), "Mr. A");
    }
}
