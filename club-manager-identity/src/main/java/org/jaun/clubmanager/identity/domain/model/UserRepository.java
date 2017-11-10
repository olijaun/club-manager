package org.jaun.clubmanager.identity.domain.model;

public interface UserRepository {
    User getUser(UserId id);
}
