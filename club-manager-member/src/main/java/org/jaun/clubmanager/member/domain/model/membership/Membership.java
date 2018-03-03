package org.jaun.clubmanager.member.domain.model.membership;

import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.member.domain.model.member.MemberId;

import java.time.LocalDate;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class Membership extends Entity<MembershipId> {

    enum Type {
        STANDARD,
        BENEVOLENT,
        PASSIVE
    }

    private final MembershipId id;
    private final int year;
    private final Type type;
    private LocalDate dateOfPayment;

    private Collection<MemberId> members;

    public Membership(MembershipId id, int year, Type type) {
        this.id = requireNonNull(id);
        this.year = requireNonNull(year);
        this.type = requireNonNull(type);
    }

    public MembershipId getId() {
        return id;
    }

    public void recordPayment(LocalDate dateOfPayment) {
        // TODO: here it gets interesting
    }
}
