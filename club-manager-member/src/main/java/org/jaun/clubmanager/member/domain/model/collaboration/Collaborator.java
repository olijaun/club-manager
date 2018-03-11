package org.jaun.clubmanager.member.domain.model.collaboration;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.ValueObject;

public abstract class Collaborator extends ValueObject {

    private final CollaboratorId id;
    private final String name;

    public Collaborator(CollaboratorId id, String name) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
    }
}
