package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class SubscriptionViewDTO implements Serializable {

    private String subscriptionId;

    private String memberId;
    private String memberFirstName;
    private String memberLastName;

    private String subscriptionPeriodId;
    private String subscriptionPeriodName;

    private String subscriptionTypeId;
    private String subscriptionTypeName;

    private String membershipTypeId;
    private String membershipTypeName;

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public SubscriptionViewDTO setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public String getMemberId() {
        return memberId;
    }

    public SubscriptionViewDTO setMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public String getSubscriptionTypeId() {
        return subscriptionTypeId;
    }

    public SubscriptionViewDTO setSubscriptionTypeId(String subscriptionTypeId) {
        this.subscriptionTypeId = subscriptionTypeId;
        return this;
    }

    public String getSubscriptionPeriodId() {
        return subscriptionPeriodId;
    }

    public SubscriptionViewDTO setSubscriptionPeriodId(String subscriptionPeriodId) {
        this.subscriptionPeriodId = subscriptionPeriodId;
        return this;
    }

    public String getMemberFirstName() {
        return memberFirstName;
    }

    public SubscriptionViewDTO setMemberFirstName(String memberFirstName) {
        this.memberFirstName = memberFirstName;
        return this;
    }

    public String getMemberLastName() {
        return memberLastName;
    }

    public SubscriptionViewDTO setMemberLastName(String memberLastName) {
        this.memberLastName = memberLastName;
        return this;
    }

    public String getSubscriptionPeriodName() {
        return subscriptionPeriodName;
    }

    public SubscriptionViewDTO setSubscriptionPeriodName(String subscriptionPeriodName) {
        this.subscriptionPeriodName = subscriptionPeriodName;
        return this;
    }

    public String getSubscriptionTypeName() {
        return subscriptionTypeName;
    }

    public SubscriptionViewDTO setSubscriptionTypeName(String subscriptionTypeName) {
        this.subscriptionTypeName = subscriptionTypeName;
        return this;
    }

    public String getMembershipTypeId() {
        return membershipTypeId;
    }

    public SubscriptionViewDTO setMembershipTypeId(String membershipTypeId) {
        this.membershipTypeId = membershipTypeId;
        return this;
    }

    public String getMembershipTypeName() {
        return membershipTypeName;
    }

    public SubscriptionViewDTO setMembershipTypeName(String membershipTypeName) {
        this.membershipTypeName = membershipTypeName;
        return this;
    }
}
