package org.jaun.clubmanager.member.domain.model.member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;


public class Subscriptions implements Iterable {
    private ArrayList<Subscription> subscriptions = new ArrayList<>();

    public Subscriptions() {
    }

    public Subscriptions(Collection<Subscription> subscriptions) {
        this.subscriptions = new ArrayList<>(subscriptions);
    }

    public void add(Subscription subscription) {
        subscriptions.add(subscription);
    }

    public boolean containsMembershipWith(SubscriptionPeriodId subscriptionPeriodId, SubscriptionTypeId subscriptionTypeId) {
        return subscriptions.stream()
                .filter(m -> m.matchesPeriodAndOption(subscriptionPeriodId, subscriptionTypeId))
                .findFirst()
                .isPresent();
    }

    @Override
    public Iterator iterator() {
        return subscriptions.iterator();
    }

    public Collection<Subscription> getSubscriptions() {
        return subscriptions;
    }
}
