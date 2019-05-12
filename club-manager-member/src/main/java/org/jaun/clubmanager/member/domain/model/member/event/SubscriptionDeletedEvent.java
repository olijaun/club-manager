package org.jaun.clubmanager.member.domain.model.member.event;

import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;

public class SubscriptionDeletedEvent extends MemberEvent {

    private final SubscriptionId subscriptionId;
    private final MemberId memberId;

    public SubscriptionDeletedEvent(SubscriptionId subscriptionId, MemberId memberId) {
        this.subscriptionId = subscriptionId;
        this.memberId = memberId;
    }

    public SubscriptionId getSubscriptionId() {
        return subscriptionId;
    }

    public MemberId getMemberId() {
        return memberId;
    }
}
