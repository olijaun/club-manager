package org.jaun.clubmanager.member.domain.model.membershiptype;

import static java.util.Objects.requireNonNull;

import org.jaun.clubmanager.domain.model.commons.Aggregate;

/**
 * Type of the Mebership regardless of Membership period. E.g. 'Gönner', 'Normal', 'Passiv'
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

    public String getDescription() {
        return description;
    }

    public MembershipTypeId getId() {
        return id;
    }
}
