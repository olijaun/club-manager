package org.jaun.clubmanager.domain.model;

import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.domain.model.commons.ValueObject;

public class User extends Entity<UserId> {
    private final UserId id;
    private final String name;

    public User(UserId id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserId getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
