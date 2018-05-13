package org.jaun.clubmanager.member.domain.model.member;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.SubscriptionOptionId;


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

    public boolean containsSubscriptionWith(MembershipPeriodId membershipPeriodId, SubscriptionOptionId subscriptionOptionId) {
        return subscriptions.stream()
                .filter(m -> m.matchesPeriodAndOption(membershipPeriodId, subscriptionOptionId))
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
