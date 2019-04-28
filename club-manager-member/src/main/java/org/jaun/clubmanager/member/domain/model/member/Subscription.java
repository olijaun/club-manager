package org.jaun.clubmanager.member.domain.model.member;

import com.google.common.collect.ImmutableList;
import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

// http://chrislema.com/memberships-and-subscriptions/
public class Subscription extends Entity<SubscriptionId> {

    private final SubscriptionId id;
    private final SubscriptionTypeId subscriptionTypeId;
    private final SubscriptionPeriodId subscriptionPeriodId;
    private final MemberId memberId;
    private final Collection<MemberId> additionalMemberIds;

    private Subscription(Builder builder) {

        this.id = requireNonNull(builder.id);
        this.subscriptionPeriodId = requireNonNull(builder.subscriptionPeriodId);
        this.subscriptionTypeId = requireNonNull(builder.subscriptionTypeId);
        this.memberId = requireNonNull(builder.memberId);
        this.additionalMemberIds = ImmutableList.copyOf(requireNonNull(builder.additionalMemberIds));
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

    public boolean matchesPeriodAndType(SubscriptionPeriodId subscriptionPeriodId, SubscriptionTypeId subscriptionTypeId) {
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
        private ArrayList<MemberId> additionalMemberIds = new ArrayList<>();

        public Subscription build() {
            return new Subscription(this);
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
            this.additionalMemberIds = new ArrayList<>(additionalMemberIds);
            return this;
        }
    }
}
