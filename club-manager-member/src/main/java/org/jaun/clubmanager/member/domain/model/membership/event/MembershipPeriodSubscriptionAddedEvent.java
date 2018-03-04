package org.jaun.clubmanager.member.domain.model.membership.event;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membership.SubscriptionDefinitionId;
import org.jaun.clubmanager.member.domain.model.membership.SubscriptionId;

import java.util.Collection;

public class MembershipPeriodSubscriptionAddedEvent extends DomainEvent<MembershipPeriodEventType> {

    private final MembershipPeriodId membershipPeriodId;
    private final SubscriptionId subscriptionId;
    private final ContactId subscriberId;
    private final Collection<ContactId> additionalSubscribers;
    private final SubscriptionDefinitionId subscriptionDefinitionId;

    public MembershipPeriodSubscriptionAddedEvent(MembershipPeriodId membershipPeriodId, SubscriptionId subscriptionId, ContactId subscriberId, Collection<ContactId> additionalSubscribers, SubscriptionDefinitionId subscriptionDefinitionId) {

        super(MembershipPeriodEventType.SUBSCRIPTION_ADDED);
        this.membershipPeriodId = membershipPeriodId;
        this.subscriberId = subscriberId;
        this.subscriptionId = subscriptionId;
        this.additionalSubscribers = additionalSubscribers;
        this.subscriptionDefinitionId = subscriptionDefinitionId;
    }

    public MembershipPeriodId getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public SubscriptionId getSubscriptionId() {
        return subscriptionId;
    }

    public ContactId getSubscriberId() {
        return subscriberId;
    }

    public Collection<ContactId> getAdditionalSubscribers() {
        return additionalSubscribers;
    }

    public SubscriptionDefinitionId getSubscriptionDefinitionId() {
        return subscriptionDefinitionId;
    }
}
