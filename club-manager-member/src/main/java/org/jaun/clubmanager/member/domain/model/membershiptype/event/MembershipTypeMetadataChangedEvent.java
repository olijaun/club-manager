package org.jaun.clubmanager.member.domain.model.membershiptype.event;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;

public class MembershipTypeMetadataChangedEvent extends MembershipTypeEvent {

    private final MembershipTypeId membershipTypeId;
    private final String name;
    private final String description;

    public MembershipTypeMetadataChangedEvent(MembershipTypeId membershipTypeId, String name, String description) {
        this.membershipTypeId = requireNonNull(membershipTypeId);
        this.name = requireNonNull(name);
        this.description = description;
    }

    public MembershipTypeId getMembershipTypeId() {
        return membershipTypeId;
    }

    public String getName() {
        return name;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }
}
