package org.jaun.clubmanager.member.domain.model.member;

import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;

public class SubscriptionOfSamePeriodAndTypeExistsException extends Exception {

    public SubscriptionOfSamePeriodAndTypeExistsException(SubscriptionPeriodId subscriptionPeriodId, SubscriptionTypeId subscriptionTypeId) {
        super("Cannot create a subscription for the same period (" + subscriptionPeriodId + ") and type (" + subscriptionTypeId + ") twice.");
    }
}
