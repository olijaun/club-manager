package org.jaun.clubmanager.member.domain.model.membership;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodMetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodSubscriptionDefinitionAddedEvent;

import com.google.common.collect.ImmutableList;

public class MembershipPeriod extends EventSourcingAggregate<MembershipPeriodId> {

    private MembershipPeriodId id;
    private LocalDate start;
    private LocalDate end;
    private String name;
    private String description;

    private List<SubscriptionDefinition> subscriptionDefinitions = new ArrayList();

    public MembershipPeriod(MembershipPeriodId id, LocalDate start, LocalDate end) {

        apply(new MembershipPeriodCreatedEvent(id, start, end));
    }

    public MembershipPeriod(EventStream<MembershipPeriod> eventStream) {
        replayEvents(eventStream);
    }

    public Collection<SubscriptionDefinition> getSubscriptionDefinitions() {
        return ImmutableList.copyOf(subscriptionDefinitions);
    }

    public Optional<SubscriptionDefinition> getSubscriptionDefinitionById(SubscriptionDefinitionId id) {
        return subscriptionDefinitions.stream().filter(d -> d.getId().equals(id)).findAny();
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

    private void mutate(MembershipPeriodSubscriptionDefinitionAddedEvent event) {
        SubscriptionDefinition def =
                new SubscriptionDefinition(event.getSubscriptionDefinitionId(), event.getMembershipTypeId(), event.getName(),
                        event.getAmount(), event.getCurrency(), event.getMaxSubscribers());

        subscriptionDefinitions.add(def);
    }


    @Override
    protected void mutate(DomainEvent event) {
        if (event instanceof MembershipPeriodCreatedEvent) {
            mutate((MembershipPeriodCreatedEvent) event);
        } else if (event instanceof MembershipPeriodMetadataChangedEvent) {
            mutate((MembershipPeriodMetadataChangedEvent) event);
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

    public void addDefinition(SubscriptionDefinitionId subscriptionDefinitionId, MembershipTypeId membershipTypeId, String name,
            double amount, Currency currency, int maxSubscribers) {

        apply(new MembershipPeriodSubscriptionDefinitionAddedEvent(id, subscriptionDefinitionId, membershipTypeId, name, amount,
                currency, maxSubscribers));

    }
}
