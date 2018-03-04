package org.jaun.clubmanager.member.domain.model.membership;

import com.google.common.collect.ImmutableList;
import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipCreatedEvent;

import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class Membership extends EventSourcingAggregate<MembershipId> {

    private MembershipId id;
    private SubscriptionDefinitionId subscriptionDefinitionId;
    private Collection<ContactId> contactIds;

    public Membership(MembershipId id, SubscriptionDefinitionId subscriptionDefinitionId, Collection<ContactId> contactIds) {
        apply(new MembershipCreatedEvent(id, subscriptionDefinitionId, contactIds));
    }

    public SubscriptionDefinitionId getSubscriptionDefinitionId() {
        return subscriptionDefinitionId;
    }

    public Collection<ContactId> getContactIds() {
        return contactIds;
    }

    protected void mutate(MembershipCreatedEvent event) {
        this.id = event.getMembershipId();
        this.subscriptionDefinitionId = event.getSubscriptionDefinitionId();
        this.contactIds = ImmutableList.copyOf(event.getContactIds());
    }

    @Override
    protected void mutate(DomainEvent event) {
        if(event instanceof MembershipCreatedEvent) {
            mutate((MembershipCreatedEvent)event);
        }
    }

    public MembershipId getId() {
        return id;
    }

}
