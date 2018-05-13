package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class MembershipViewDTO implements Serializable {

    private String membershipId;
    private String subscriberId;
    private String subscriberFirstName;
    private String subscriberLastName;
    private String membershipPeriodId;
    private String membershipPeriodName;
    private String subscriptionOptionId;
    private String subscriptionOptionName;
    private String membershipTypeId;
    private String membershipTypeName;

    public String getMembershipId() {
        return membershipId;
    }

    public MembershipViewDTO setMembershipId(String membershipId) {
        this.membershipId = membershipId;
        return this;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public MembershipViewDTO setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
        return this;
    }

    public String getSubscriptionOptionId() {
        return subscriptionOptionId;
    }

    public MembershipViewDTO setSubscriptionOptionId(String subscriptionOptionId) {
        this.subscriptionOptionId = subscriptionOptionId;
        return this;
    }

    public String getMembershipPeriodId() {
        return membershipPeriodId;
    }

    public MembershipViewDTO setMembershipPeriodId(String membershipPeriodId) {
        this.membershipPeriodId = membershipPeriodId;
        return this;
    }

    public String getSubscriberFirstName() {
        return subscriberFirstName;
    }

    public MembershipViewDTO setSubscriberFirstName(String subscriberFirstName) {
        this.subscriberFirstName = subscriberFirstName;
        return this;
    }

    public String getSubscriberLastName() {
        return subscriberLastName;
    }

    public MembershipViewDTO setSubscriberLastName(String subscriberLastName) {
        this.subscriberLastName = subscriberLastName;
        return this;
    }

    public String getMembershipPeriodName() {
        return membershipPeriodName;
    }

    public MembershipViewDTO setMembershipPeriodName(String membershipPeriodName) {
        this.membershipPeriodName = membershipPeriodName;
        return this;
    }

    public String getSubscriptionOptionName() {
        return subscriptionOptionName;
    }

    public MembershipViewDTO setSubscriptionOptionName(String subscriptionOptionName) {
        this.subscriptionOptionName = subscriptionOptionName;
        return this;
    }

    public String getMembershipTypeId() {
        return membershipTypeId;
    }

    public MembershipViewDTO setMembershipTypeId(String membershipTypeId) {
        this.membershipTypeId = membershipTypeId;
        return this;
    }

    public String getMembershipTypeName() {
        return membershipTypeName;
    }

    public MembershipViewDTO setMembershipTypeName(String membershipTypeName) {
        this.membershipTypeName = membershipTypeName;
        return this;
    }
}
