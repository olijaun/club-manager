package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import com.google.common.collect.ImmutableList;
import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Objects.requireNonNull;

public class SubscriptionRequest extends Entity<SubscriptionId> {

    private final SubscriptionId subscriptionId;
    private final SubscriptionPeriodId subscriptionPeriodId;
    private final SubscriptionTypeId subscriptionTypeId;
    private final Collection<MemberId> additionalSubscriberIds;

    /**
     * this constructor is intentionally package protected so that only the SubscriptionPeriod can create it
     */
    private SubscriptionRequest(Builder builder) {
        this.subscriptionId = requireNonNull(builder.subscriptionId);
        this.subscriptionPeriodId = requireNonNull(builder.subscriptionPeriodId);
        this.subscriptionTypeId = requireNonNull(builder.subscriptionTypeId);
        this.additionalSubscriberIds = ImmutableList.copyOf(builder.additionalSubscriberIds);
    }

    public SubscriptionPeriodId getSubscriptionPeriodId() {
        return subscriptionPeriodId;
    }

    public SubscriptionTypeId getSubscriptionTypeId() {
        return subscriptionTypeId;
    }

    public Collection<MemberId> getAdditionalSubscriberIds() {
        return additionalSubscriberIds;
    }

    @Override
    public SubscriptionId getId() {
        return subscriptionId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SubscriptionId subscriptionId;
        private SubscriptionPeriodId subscriptionPeriodId;
        private SubscriptionTypeId subscriptionTypeId;
        private Collection<MemberId> additionalSubscriberIds = new ArrayList<>();

        public SubscriptionRequest build() {
            return new SubscriptionRequest(this);
        }

        public Builder subscriptionId(SubscriptionId subscriptionId) {
            this.subscriptionId = subscriptionId;
            return this;
        }

        public Builder subscriptionPeriodId(SubscriptionPeriodId subscriptionPeriodId) {
            this.subscriptionPeriodId = subscriptionPeriodId;
            return this;
        }

        public Builder subscriptionTypeId(SubscriptionTypeId subscriptionTypeId) {
            this.subscriptionTypeId = subscriptionTypeId;
            return this;
        }

        public Builder additionalSubscriberIds(Collection<MemberId> additionalSubscriberIds) {
            this.additionalSubscriberIds = new ArrayList<>(additionalSubscriberIds);
            return this;
        }
    }

}
