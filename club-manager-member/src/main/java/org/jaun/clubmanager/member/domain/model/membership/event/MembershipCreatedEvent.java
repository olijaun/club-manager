package org.jaun.clubmanager.member.domain.model.membership.event;

import com.google.common.collect.ImmutableList;
import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membership.SubscriptionDefinition;
import org.jaun.clubmanager.member.domain.model.membership.SubscriptionDefinitionId;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class MembershipCreatedEvent extends DomainEvent<MembershipEventType> {

    private final MembershipId membershipId;
    private final SubscriptionDefinitionId subscriptionDefinitionId;
    private final Collection<ContactId> contactIds;

    public MembershipCreatedEvent(MembershipId membershipId, SubscriptionDefinitionId subscriptionDefinitionId, Collection<ContactId> contactIds) {
        super(MembershipEventType.MEMBERSHIP_CREATED);
        this.membershipId = requireNonNull(membershipId);
        this.subscriptionDefinitionId = requireNonNull(subscriptionDefinitionId);
        this.contactIds = ImmutableList.copyOf(contactIds);
    }

    public MembershipId getMembershipId() {
        return membershipId;
    }

    public SubscriptionDefinitionId getSubscriptionDefinitionId() {
        return subscriptionDefinitionId;
    }

    public Collection<ContactId> getContactIds() {
        return contactIds;
    }
}
