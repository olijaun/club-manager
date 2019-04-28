package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

public class NoSuchSubscriptionTypeForPeriod extends Exception {

    public NoSuchSubscriptionTypeForPeriod(SubscriptionPeriodId subscriptionPeriodId, SubscriptionTypeId subscriptionTypeId) {
        super("subscription type " + subscriptionTypeId + " does not exist in period " + subscriptionPeriodId);
    }
}
