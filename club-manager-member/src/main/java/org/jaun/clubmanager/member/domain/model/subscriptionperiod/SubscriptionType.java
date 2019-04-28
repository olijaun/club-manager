package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;

import java.util.Currency;

import static java.util.Objects.requireNonNull;

public class SubscriptionType extends Entity<SubscriptionTypeId> {

    private final SubscriptionTypeId subscriptionTypeId;
    private final MembershipTypeId membershipTypeId; // e.g. Gönner, Normal, Passiv
    private final String name;
    private final double amount;
    private final Currency currency;
    private final int maxSubscribers;

    private SubscriptionType(Builder builder) {

        this.subscriptionTypeId = requireNonNull(builder.subscriptionTypeId);
        this.membershipTypeId = requireNonNull(builder.membershipTypeId);
        this.name = requireNonNull(builder.name);
        this.amount = requireNonNull(builder.amount);
        this.currency = requireNonNull(builder.currency);
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

    public double getAmount() {
        return amount;
    }

    public Currency getCurrency() {
        return currency;
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
        private Double amount;
        private Currency currency;
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

        public Builder amount(Double amount) {
            this.amount = amount;
            return this;
        }

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder maxSubscribers(Integer maxSubscribers) {
            this.maxSubscribers = maxSubscribers;
            return this;
        }
    }
}
