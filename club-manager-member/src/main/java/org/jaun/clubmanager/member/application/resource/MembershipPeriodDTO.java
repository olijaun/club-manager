package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membership.Subscription;
import org.jaun.clubmanager.member.domain.model.membership.SubscriptionDefinition;

import java.io.Serializable;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class MembershipPeriodDTO implements Serializable {

    private String id;
    private String startDate;
    private String endDate;
    private String name;
    private String description;

//    private List<SubscriptionDefinition> subscriptionDefinitions = new ArrayList();
//
//    private List<Subscription> subscriptions = new ArrayList<>();


    public String getId() {
        return id;
    }

    public MembershipPeriodDTO setId(String id) {
        this.id = id;
        return this;
    }

    public String getStartDate() {
        return startDate;
    }

    public MembershipPeriodDTO setStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public String getEndDate() {
        return endDate;
    }

    public MembershipPeriodDTO setEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getName() {
        return name;
    }

    public MembershipPeriodDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public MembershipPeriodDTO setDescription(String description) {
        this.description = description;
        return this;
    }
}
