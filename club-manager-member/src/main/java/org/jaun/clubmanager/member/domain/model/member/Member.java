package org.jaun.clubmanager.member.domain.model.member;

import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.member.event.MemberCreatedEvent;
import org.jaun.clubmanager.member.domain.model.member.event.MemberEvent;
import org.jaun.clubmanager.member.domain.model.member.event.SubscriptionCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershipperiod.SubscriptionRequest;

public class Member extends EventSourcingAggregate<MemberId, MemberEvent> {

    private MemberId id;
    private Subscriptions subscriptions = new Subscriptions();

    public Member(MemberId id, String firstName, String lastNameOrCompanyName) {
        apply(new MemberCreatedEvent(id, firstName, lastNameOrCompanyName));
    }

    public Member(EventStream<MemberEvent> eventStream) {
        replayEvents(eventStream);
    }

    public SubscriptionId subscribe(SubscriptionRequest subscriptionRequest) {

        SubscriptionId subscriptionId = SubscriptionId.random(SubscriptionId::new);

        if (subscriptions.containsSubscriptionWith(subscriptionRequest.getMembershipPeriodId(),
                subscriptionRequest.getSubscriptionOptionId())) {
            throw new IllegalStateException("Cannot create a subscription for the same period and option twice.");
        }

        apply(new SubscriptionCreatedEvent(subscriptionId, id,
                subscriptionRequest.getMembershipPeriodId(), subscriptionRequest.getSubscriptionOptionId(),
                subscriptionRequest.getAdditionalSubscriberIds()));

        return subscriptionId;
    }

    protected void mutate(MemberCreatedEvent event) {
        this.id = event.getMemberId();
    }

    protected void mutate(SubscriptionCreatedEvent event) {
        Subscription subscription =
                new Subscription(event.getSubscriptionId(), event.getMembershipPeriodId(), event.getSubscriptionOptionId(),
                        event.getMemberId(), event.getAdditionalSubscriberIds());

        subscriptions.add(subscription);
    }

    public Subscriptions getSubscriptions() {
        return subscriptions;
    }

    @Override
    protected void mutate(MemberEvent event) {
        if (event instanceof MemberCreatedEvent) {
            mutate((MemberCreatedEvent) event);
        } else if (event instanceof SubscriptionCreatedEvent) {
            mutate((SubscriptionCreatedEvent) event);
        }
    }

    @Override
    public MemberId getId() {
        return id;
    }
}
