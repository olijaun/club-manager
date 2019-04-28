package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import com.google.common.collect.ImmutableList;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.MetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionTypeAddedEvent;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    public Optional<SubscriptionType> getSubscriptionTypeById(SubscriptionTypeId id) {
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
                SubscriptionType.builder()
                        .subscriptionTypeId(event.getSubscriptionTypeId())
                        .membershipTypeId(event.getMembershipTypeId())
                        .name(event.getName())
                        .amount(event.getAmount())
                        .currency(event.getCurrency())
                        .maxSubscribers(event.getMaxSubscribers()).build();

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
                                                         Collection<Member> additionalSubscribers) throws NoSuchSubscriptionTypeForPeriod {

        SubscriptionType option = getSubscriptionTypeById(subscriptionTypeId).orElseThrow(
                () -> new NoSuchSubscriptionTypeForPeriod(id, subscriptionTypeId));

        if ((additionalSubscribers.size() + 1) > option.getMaxSubscribers()) {
            throw new IllegalStateException(
                    "a maximum of " + option.getMaxSubscribers() + " is possible for this subscription type");
        }

        Set<MemberId> additionalSubscriberMemberIds = additionalSubscribers.stream().map(Member::getId).collect(Collectors.toSet());

        return new SubscriptionRequest.Builder()
                .subscriptionId(subscriptionId)
                .subscriptionPeriodId(id)
                .subscriptionTypeId(subscriptionTypeId)
                .additionalSubscriberIds(additionalSubscriberMemberIds).build();

    }
}
