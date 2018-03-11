package org.jaun.clubmanager.member.domain.model.membership;

import java.util.Collection;

import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;

import com.google.common.collect.ImmutableList;

public class Subscription extends Entity<SubscriptionId> {

    private final SubscriptionId id;
    private final ContactId subscriberId;
    private final SubscriptionDefinitionId subscriptionDefinitionId;
    private final Collection<ContactId> additionalSubscribers;

    public Subscription(SubscriptionId id, ContactId subscriberId, Collection<ContactId> additionalSubscribers,
            SubscriptionDefinitionId subscriptionDefinitionId) {
        this.id = id;
        this.subscriberId = subscriberId;
        this.subscriptionDefinitionId = subscriptionDefinitionId;
        this.additionalSubscribers = ImmutableList.copyOf(additionalSubscribers);
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

    @Override
    public SubscriptionId getId() {
        return id;
    }
}
