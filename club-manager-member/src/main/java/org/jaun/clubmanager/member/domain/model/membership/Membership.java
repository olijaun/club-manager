package org.jaun.clubmanager.member.domain.model.membership;

import com.google.common.collect.ImmutableList;
import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class Membership extends Entity<MembershipId> {

    private final MembershipId id;
    private MembershipPeriodId membershipPeriodId;
    private Collection<ContactId> contactIds;

    public Membership(MembershipId id, MembershipPeriodId membershipPeriodId, Collection<ContactId> contactIds) {
        this.id = requireNonNull(id);
        this.membershipPeriodId = requireNonNull(membershipPeriodId);
        this.contactIds = ImmutableList.copyOf(contactIds);
    }

    public MembershipPeriodId getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public Collection<ContactId> getContactIds() {
        return contactIds;
    }

    public MembershipId getId() {
        return id;
    }

}
