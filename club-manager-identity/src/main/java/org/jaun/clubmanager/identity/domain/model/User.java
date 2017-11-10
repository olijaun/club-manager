package org.jaun.clubmanager.identity.domain.model;

import org.jaun.clubmanager.domain.model.commons.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.util.Objects.requireNonNull;

public class User extends Entity<UserId> {
    private final UserId id;
    private final String name;
    private final Collection<Object> roles;

    public User(UserId id, String name, Collection<Role> roles) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        if (roles == null) {
            this.roles = Collections.emptyList();
        } else {
            this.roles = Collections.unmodifiableList(new ArrayList<>(roles));
        }
    }

    public UserId getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isUserInRole(Role role) {
        return roles.contains(role);
    }
}
