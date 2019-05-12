package org.jaun.clubmanager.member.domain.model.member;

import java.util.*;
import java.util.stream.Collectors;

import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionRequest;
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

    public boolean remove(SubscriptionId subscriptionId) {
        return getById(subscriptionId).map(this::remove).orElse(false);
    }

    public boolean remove(Subscription subscription) {
        return subscriptions.remove(subscription);
    }

    public boolean contains(Subscription subscription) {
        return subscriptions.stream().filter(s -> s.getId().equals(subscription.getId())).findFirst().isPresent();
    }

    public boolean containsId(SubscriptionId subscriptionId) {
        return subscriptions.stream().map(Subscription::getId).filter(id -> subscriptionId.equals(id)).findFirst().isPresent();
    }

    public Optional<Subscription> getById(SubscriptionId subscriptionId) {
        return subscriptions.stream().filter(s -> s.getId().equals(subscriptionId)).findFirst();
    }

    public boolean containsMembershipWith(SubscriptionPeriodId subscriptionPeriodId, SubscriptionTypeId subscriptionTypeId) {
        return subscriptions.stream()
                .filter(m -> m.matchesPeriodAndType(subscriptionPeriodId, subscriptionTypeId))
                .findFirst()
                .isPresent();
    }

    public int size() {
        return subscriptions.size();
    }

    public Optional<Subscription> first() {
        if(subscriptions.size() == 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(subscriptions.iterator().next());
    }

    @Override
    public Iterator<Subscription> iterator() {
        return subscriptions.iterator();
    }

    public Collection<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public Collection<Subscription> getRemovals(List<SubscriptionRequest> subscriptionRequests) {

        List<SubscriptionId> requestIds =
                subscriptionRequests.stream().map(r -> r.getId()).collect(Collectors.toList());

        return subscriptions.stream()
                .filter(s -> !requestIds.contains(s.getId()))
                .collect(Collectors.toList());
    }
}
