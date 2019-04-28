package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;

import static java.util.Objects.requireNonNull;

public class SubscriptionType extends Entity<SubscriptionTypeId> {

    private final SubscriptionTypeId subscriptionTypeId;
    private final MembershipTypeId membershipTypeId; // e.g. Gönner, Normal, Passiv
    private final String name;
    private final Money price;
    private final int maxSubscribers;

    private SubscriptionType(Builder builder) {

        this.subscriptionTypeId = requireNonNull(builder.subscriptionTypeId);
        this.membershipTypeId = requireNonNull(builder.membershipTypeId);
        this.name = requireNonNull(builder.name);
        this.price = requireNonNull(builder.price);
        this.maxSubscribers = requireNonNull(builder.maxSubscribers);
    }

    public SubscriptionTypeId getSubscriptionTypeId() {
        return subscriptionTypeId;
    }

    public MembershipTypeId getMembershipTypeId() {
        return membershipTypeId;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public SubscriptionTypeId getId() {
        return subscriptionTypeId;
    }

    public int getMaxSubscribers() {
        return maxSubscribers;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private SubscriptionTypeId subscriptionTypeId;
        private MembershipTypeId membershipTypeId; // e.g. Gönner, Normal, Passiv
        private String name;
        private Money price;
        private Integer maxSubscribers;

        public SubscriptionType build() {
            return new SubscriptionType(this);
        }

        public Builder subscriptionTypeId(SubscriptionTypeId subscriptionTypeId) {
            this.subscriptionTypeId = subscriptionTypeId;
            return this;
        }

        public Builder membershipTypeId(MembershipTypeId membershipTypeId) {
            this.membershipTypeId = membershipTypeId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder maxSubscribers(Integer maxSubscribers) {
            this.maxSubscribers = maxSubscribers;
            return this;
        }
    }
}
