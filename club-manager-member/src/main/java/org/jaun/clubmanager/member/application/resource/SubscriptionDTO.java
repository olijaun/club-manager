package org.jaun.clubmanager.member.application.resource;

public class SubscriptionDTO {
    private String subscriptionId;
    private String subscriberId;
    //private Collection<ContactId> additionalSubscribers;
    private String subscriptionDefinitionId;

    public String getSubscriptionId() {
        return subscriptionId;
    }

    public SubscriptionDTO setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    public String getSubscriberId() {
        return subscriberId;
    }

    public SubscriptionDTO setSubscriberId(String subscriberId) {
        this.subscriberId = subscriberId;
        return this;
    }

    public String getSubscriptionDefinitionId() {
        return subscriptionDefinitionId;
    }

    public SubscriptionDTO setSubscriptionDefinitionId(String subscriptionDefinitionId) {
        this.subscriptionDefinitionId = subscriptionDefinitionId;
        return this;
    }
}
