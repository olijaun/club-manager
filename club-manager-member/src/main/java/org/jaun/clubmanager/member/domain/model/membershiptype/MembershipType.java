package org.jaun.clubmanager.member.domain.model.membershiptype;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.Aggregate;

/**
 * Type of the Membership regardless of Subscription period. E.g. 'Gönner', 'Normal', 'Passiv'
 */
public class MembershipType extends Aggregate<MembershipTypeId> {

    private MembershipTypeId id;
    private String name;
    private String description;

    public MembershipType(MembershipTypeId id, String name, String description) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public MembershipTypeId getId() {
        return id;
    }
}
