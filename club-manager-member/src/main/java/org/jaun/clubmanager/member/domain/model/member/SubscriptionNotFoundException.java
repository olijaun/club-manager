package org.jaun.clubmanager.member.domain.model.member;

public class SubscriptionNotFoundException extends Exception {
    public SubscriptionNotFoundException(SubscriptionId subscriptionId) {
        super("Could not find subscription: " + subscriptionId);
    }
}
