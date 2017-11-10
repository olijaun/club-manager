package org.jaun.clubmanager.identity.domain.model;

import javax.ejb.Stateless;
import java.util.Arrays;

import static java.util.Arrays.asList;

@Stateless
public class InMemoryMockUserRepository implements UserRepository {

    public User getUser(UserId id) {
        return new User(new UserId(id.getValue()), "Mr. A", asList(Role.ADMIN, Role.BOARD_MEMBER));
    }
}
