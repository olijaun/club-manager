package org.jaun.clubmanager.member.domain.model.membership;

import com.google.common.collect.ImmutableList;
import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodMetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodSubscriptionAddedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodSubscriptionDefinitionAddedEvent;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class MembershipPeriod extends EventSourcingAggregate<MembershipPeriodId> {

    private MembershipPeriodId id;
    private LocalDate start;
    private LocalDate end;
    private String name;
    private String description;

    private List<SubscriptionDefinition> subscriptionDefinitions = new ArrayList();

    private List<Subscription> subscriptions = new ArrayList<>();

    public MembershipPeriod(MembershipPeriodId id, LocalDate start, LocalDate end) {

        apply(new MembershipPeriodCreatedEvent(id, start, end));
    }

    public MembershipPeriod(EventStream<MembershipPeriod> eventStream) {
        replayEvents(eventStream);
    }

    public Collection<SubscriptionDefinition> getSubscriptionDefinitions() {
        return ImmutableList.copyOf(subscriptionDefinitions);
    }

    public void updateMetadata(String name, String description) {
        apply(new MembershipPeriodMetadataChangedEvent(id, name, description));
    }

    private void mutate(MembershipPeriodCreatedEvent event) {
        this.id = event.getMembershipPeriodId();
        this.start = event.getStart();
        this.end = event.getEnd();
    }

    private void mutate(MembershipPeriodMetadataChangedEvent event) {
        this.id = event.getMembershipPeriodId();
        this.name = event.getName();
        this.description = event.getDescription();
    }

    private void mutate(MembershipPeriodSubscriptionAddedEvent event) {
        Subscription subscription = new Subscription(event.getSubscriptionId(), event.getSubscriberId(), event.getAdditionalSubscribers(), event.getSubscriptionDefinitionId());

        subscriptions.add(subscription);
    }

    private void mutate(MembershipPeriodSubscriptionDefinitionAddedEvent event) {
        SubscriptionDefinition def = new SubscriptionDefinition(event.getSubscriptionDefinitionId(), event.getMembershipTypeId(), event.getName(), event.getAmount(), event.getCurrency(), event.getMaxSubscribers());

        subscriptionDefinitions.add(def);
    }


    @Override
    protected void mutate(DomainEvent event) {
        if (event instanceof MembershipPeriodCreatedEvent) {
            mutate((MembershipPeriodCreatedEvent) event);
        } else if (event instanceof MembershipPeriodMetadataChangedEvent) {
            mutate((MembershipPeriodMetadataChangedEvent) event);
        } else if (event instanceof MembershipPeriodSubscriptionAddedEvent) {
            mutate((MembershipPeriodSubscriptionAddedEvent) event);
        } else if (event instanceof MembershipPeriodSubscriptionDefinitionAddedEvent) {
            mutate((MembershipPeriodSubscriptionDefinitionAddedEvent) event);
        }
    }

    @Override
    public MembershipPeriodId getId() {
        return id;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void subscribe(ContactId contactId, Collection<ContactId> additionalSubscribers, SubscriptionDefinitionId definitionId) {
        Optional<SubscriptionDefinition> definition = subscriptionDefinitions.stream().filter(d -> d.getId().equals(definitionId)).findAny();

        if (!definition.isPresent()) {
            throw new IllegalStateException("cannot subscribe " + contactId + " to unknown definition");
        }

        if ((additionalSubscribers.size() + 1) > definition.get().getMaxSubscribers()) {
            throw new IllegalStateException("a maximum of " + definition.get().getMaxSubscribers() + " is possible for this subscription type");
        }

        apply(new MembershipPeriodSubscriptionAddedEvent(id, new SubscriptionId(UUID.randomUUID().toString()), contactId, additionalSubscribers, definitionId));


    }

    public void addDefinition(SubscriptionDefinitionId subscriptionDefinitionId, MembershipTypeId membershipTypeId, String name, double amount, Currency currency, int maxSubscribers) {

        apply(new MembershipPeriodSubscriptionDefinitionAddedEvent(id, subscriptionDefinitionId, membershipTypeId, name, amount, currency, maxSubscribers));

    }

    public Collection<Subscription> getSubscriptions() {
        return subscriptions;
    }
}
