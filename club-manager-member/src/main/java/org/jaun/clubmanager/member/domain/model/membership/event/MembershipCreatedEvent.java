package org.jaun.clubmanager.member.domain.model.membership.event;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membership.SubscriptionDefinitionId;

import com.google.common.collect.ImmutableList;

public class MembershipCreatedEvent extends DomainEvent<MembershipEventType> {

    private final MembershipId membershipId;
    private final MembershipPeriodId membershipPeriodId;
    private final SubscriptionDefinitionId subscriptionDefinitionId;
    private final ContactId subscriberId;
    private final Collection<ContactId> additionalSubscriberIds;

    public MembershipCreatedEvent(MembershipId membershipId, MembershipPeriodId membershipPeriodId,
            SubscriptionDefinitionId subscriptionDefinitionId, ContactId subscriberId,
            Collection<ContactId> additionalSubscriberIds) {

        super(MembershipEventType.MEMBERSHIP_CREATED);
        this.membershipId = requireNonNull(membershipId);
        this.membershipPeriodId = requireNonNull(membershipPeriodId);
        this.subscriptionDefinitionId = requireNonNull(subscriptionDefinitionId);
        this.subscriberId = requireNonNull(subscriberId);
        this.additionalSubscriberIds = ImmutableList.copyOf(additionalSubscriberIds);
    }

    public MembershipId getMembershipId() {
        return membershipId;
    }

    public SubscriptionDefinitionId getSubscriptionDefinitionId() {
        return subscriptionDefinitionId;
    }

    public ContactId getSubscriberId() {
        return subscriberId;
    }

    public Collection<ContactId> getAdditionalSubscriberIds() {
        return additionalSubscriberIds;
    }

    public MembershipPeriodId getMembershipPeriodId() {
        return membershipPeriodId;
    }
}
