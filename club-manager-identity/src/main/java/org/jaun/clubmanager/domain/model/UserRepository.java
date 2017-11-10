package org.jaun.clubmanager.domain.model;

public interface UserRepository {
    User getUser(UserId id);
}
