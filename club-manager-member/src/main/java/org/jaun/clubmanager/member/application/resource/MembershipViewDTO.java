package org.jaun.clubmanager.member.application.resource;

public class MembershipViewDTO {

    private String membershipId;
    private String subscriberId;
    private String subscriberFirstName;
    private String subscriberLastName;
    private String membershipPeriodId;
    private String membershipPeriodName;
    private String subscriptionDefinitionId;
    private String subscriptionDefinitionName;
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

    public String getSubscriptionDefinitionId() {
        return subscriptionDefinitionId;
    }

    public MembershipViewDTO setSubscriptionDefinitionId(String subscriptionDefinitionId) {
        this.subscriptionDefinitionId = subscriptionDefinitionId;
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

    public String getSubscriptionDefinitionName() {
        return subscriptionDefinitionName;
    }

    public MembershipViewDTO setSubscriptionDefinitionName(String subscriptionDefinitionName) {
        this.subscriptionDefinitionName = subscriptionDefinitionName;
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
