package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.MetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionTypeAddedEvent;

import com.google.common.collect.ImmutableList;

public class SubscriptionPeriod extends EventSourcingAggregate<SubscriptionPeriodId, SubscriptionPeriodEvent> {

    private SubscriptionPeriodId id;
    private LocalDate start;
    private LocalDate end;
    private String name;
    private String description;

    private List<SubscriptionType> subscriptionTypes = new ArrayList();

    public SubscriptionPeriod(SubscriptionPeriodId id, LocalDate start, LocalDate end) {

        apply(new SubscriptionPeriodCreatedEvent(id, start, end));
    }

    public SubscriptionPeriod(EventStream<SubscriptionPeriodEvent> eventStream) {
        replayEvents(eventStream);
    }

    public Collection<SubscriptionType> getSubscriptionTypes() {
        return ImmutableList.copyOf(subscriptionTypes);
    }

    public Optional<SubscriptionType> getMembershipOptionById(SubscriptionTypeId id) {
        return subscriptionTypes.stream().filter(d -> d.getId().equals(id)).findAny();
    }

    public void updateMetadata(String name, String description) {
        apply(new MetadataChangedEvent(id, name, description));
    }

    private void mutate(SubscriptionPeriodCreatedEvent event) {
        this.id = event.getSubscriptionPeriodId();
        this.start = event.getStart();
        this.end = event.getEnd();
    }

    private void mutate(MetadataChangedEvent event) {
        this.id = event.getSubscriptionPeriodId();
        this.name = event.getName();
        this.description = event.getDescription();
    }

    private void mutate(SubscriptionTypeAddedEvent event) {
        SubscriptionType def =
                new SubscriptionType(event.getSubscriptionTypeId(), event.getMembershipTypeId(), event.getName(), event.getAmount(),
                        event.getCurrency(), event.getMaxSubscribers());

        subscriptionTypes.add(def);
    }


    @Override
    protected void mutate(SubscriptionPeriodEvent event) {
        if (event instanceof SubscriptionPeriodCreatedEvent) {
            mutate((SubscriptionPeriodCreatedEvent) event);
        } else if (event instanceof MetadataChangedEvent) {
            mutate((MetadataChangedEvent) event);
        } else if (event instanceof SubscriptionTypeAddedEvent) {
            mutate((SubscriptionTypeAddedEvent) event);
        }
    }

    @Override
    public SubscriptionPeriodId getId() {
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

    public void addSubscriptionType(SubscriptionTypeId subscriptionTypeId, MembershipTypeId membershipTypeId, String name,
            double amount, Currency currency, int maxSubscribers) {

        if (subscriptionTypes.stream()
                .filter(subscriptionType -> subscriptionType.getId().equals(subscriptionTypeId))
                .findAny()
                .isPresent()) {
            return;
        }

        apply(new SubscriptionTypeAddedEvent(id, subscriptionTypeId, membershipTypeId, name, amount, currency, maxSubscribers));

    }

    public SubscriptionRequest createSubscriptionRequest(SubscriptionId subscriptionId, SubscriptionTypeId subscriptionTypeId,
            Collection<Member> additionalSubscribers) {

        SubscriptionType option = getMembershipOptionById(subscriptionTypeId).orElseThrow(
                () -> new IllegalStateException("option " + subscriptionTypeId + " does not exist in period " + id));

        if ((additionalSubscribers.size() + 1) > option.getMaxSubscribers()) {
            throw new IllegalStateException(
                    "a maximum of " + option.getMaxSubscribers() + " is possible for this subscription type");
        }

        return new SubscriptionRequest(subscriptionId, id, subscriptionTypeId,
                additionalSubscribers.stream().map(Member::getId).collect(Collectors.toSet()));

    }
}
