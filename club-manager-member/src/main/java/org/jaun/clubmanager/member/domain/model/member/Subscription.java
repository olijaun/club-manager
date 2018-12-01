package org.jaun.clubmanager.member.domain.model.member;

import static java.util.Objects.requireNonNull;

import java.util.Collection;

import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;

import com.google.common.collect.ImmutableList;

// http://chrislema.com/memberships-and-subscriptions/
public class Subscription extends Entity<SubscriptionId> {

    private final SubscriptionId id;
    private final SubscriptionTypeId subscriptionTypeId;
    private final SubscriptionPeriodId subscriptionPeriodId;
    private final MemberId memberId;
    private final Collection<MemberId> additionalMemberIds;

    public Subscription(SubscriptionId id, SubscriptionPeriodId periodId, SubscriptionTypeId subscriptionTypeId, MemberId memberId,
            Collection<MemberId> additionalMemberIds) {

        this.id = requireNonNull(id);
        this.subscriptionPeriodId = requireNonNull(periodId);
        this.subscriptionTypeId = requireNonNull(subscriptionTypeId);
        this.memberId = requireNonNull(memberId);
        this.additionalMemberIds = ImmutableList.copyOf(requireNonNull(additionalMemberIds));
    }

    public SubscriptionTypeId getSubscriptionTypeId() {
        return subscriptionTypeId;
    }

    public SubscriptionPeriodId getSubscriptionPeriodId() {
        return subscriptionPeriodId;
    }

    public MemberId getMemberId() {
        return memberId;
    }

    public Collection<MemberId> getAdditionalMemberIds() {
        return additionalMemberIds;
    }

    public boolean matchesPeriodAndOption(SubscriptionPeriodId subscriptionPeriodId, SubscriptionTypeId subscriptionTypeId) {
        return this.subscriptionPeriodId.equals(subscriptionPeriodId) && this.subscriptionTypeId.equals(subscriptionTypeId);
    }

    @Override
    public SubscriptionId getId() {
        return id;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SubscriptionId id;
        private SubscriptionTypeId subscriptionTypeId;
        private SubscriptionPeriodId subscriptionPeriodId;
        private MemberId memberId;
        private Collection<MemberId> additionalMemberIds;

        public Subscription build() {
            return new Subscription(id, subscriptionPeriodId, subscriptionTypeId, memberId, additionalMemberIds);
        }

        public Builder id(SubscriptionId id) {
            this.id = id;
            return this;
        }

        public Builder subscriptionTypeId(SubscriptionTypeId subscriptionTypeId) {
            this.subscriptionTypeId = subscriptionTypeId;
            return this;
        }

        public Builder subscriptionPeriodId(SubscriptionPeriodId subscriptionPeriodId) {
            this.subscriptionPeriodId = subscriptionPeriodId;
            return this;
        }

        public Builder memberId(MemberId memberId) {
            this.memberId = memberId;
            return this;
        }

        public Builder additionalMemberIds(Collection<MemberId> additionalMemberIds) {
            this.additionalMemberIds = additionalMemberIds;
            return this;
        }
    }
}
