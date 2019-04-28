package org.jaun.clubmanager.member.domain.model.member;

import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.member.domain.model.member.event.MemberCreatedEvent;
import org.jaun.clubmanager.member.domain.model.member.event.MemberEvent;
import org.jaun.clubmanager.member.domain.model.member.event.SubscriptionCreatedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionRequest;

import java.util.ArrayList;
import java.util.List;

public class Member extends EventSourcingAggregate<MemberId, MemberEvent> {
    private MemberId id;
    private Subscriptions subscriptions = new Subscriptions();

    public Member(MemberId id) {
        apply(new MemberCreatedEvent(id));
    }

    public Member(EventStream<MemberEvent> eventStream) {
        replayEvents(eventStream);
    }

    public void subscribe(SubscriptionRequest subscriptionRequest) throws SubscriptionOfSamePeriodAndTypeExistsException {

        if (subscriptions.containsId(subscriptionRequest.getSubscriptionId())) {
            return;
        }

        if (subscriptions.containsMembershipWith(subscriptionRequest.getSubscriptionPeriodId(),
                subscriptionRequest.getSubscriptionTypeId())) {

            throw new SubscriptionOfSamePeriodAndTypeExistsException(subscriptionRequest.getSubscriptionPeriodId(), subscriptionRequest.getSubscriptionTypeId());
        }

        apply(new SubscriptionCreatedEvent(subscriptionRequest.getSubscriptionId(), id,
                subscriptionRequest.getSubscriptionPeriodId(), subscriptionRequest.getSubscriptionTypeId(),
                subscriptionRequest.getAdditionalSubscriberIds()));

    }

    private void mutate(MemberCreatedEvent event) {
        this.id = event.getMemberId();
    }

    private void mutate(SubscriptionCreatedEvent event) {
        Subscription subscription =
                new Subscription(event.getSubscriptionId(), event.getSubscriptionPeriodId(), event.getSubscriptionTypeId(),
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
