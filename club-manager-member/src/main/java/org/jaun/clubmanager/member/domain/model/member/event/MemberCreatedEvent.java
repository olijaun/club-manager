package org.jaun.clubmanager.member.domain.model.member.event;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.member.domain.model.member.MemberId;

public class MemberCreatedEvent extends MemberEvent {

    private final MemberId memberId;
    private final String firstName;
    private final String lastNameOrCompanyName;

    public MemberCreatedEvent(MemberId memberId, String firstName, String lastNameOrCompanyName) {

        this.memberId = requireNonNull(memberId);
        this.firstName = firstName;
        this.lastNameOrCompanyName = requireNonNull(lastNameOrCompanyName);
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public Optional<String> getFirstName() {
        return Optional.ofNullable(firstName);
    }

    public String getLastNameOrCompanyName() {
        return lastNameOrCompanyName;
    }
}
