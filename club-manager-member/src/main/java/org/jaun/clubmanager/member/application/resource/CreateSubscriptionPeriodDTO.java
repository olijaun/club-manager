package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class CreateSubscriptionPeriodDTO implements Serializable {

    private String startDate;
    private String endDate;
    private String name;
    private String description;

    public String getStartDate() {
        return startDate;
    }

    public CreateSubscriptionPeriodDTO setStartDate(String startDate) {
        this.startDate = startDate;
        return this;
    }

    public String getEndDate() {
        return endDate;
    }

    public CreateSubscriptionPeriodDTO setEndDate(String endDate) {
        this.endDate = endDate;
        return this;
    }

    public String getName() {
        return name;
    }

    public CreateSubscriptionPeriodDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public CreateSubscriptionPeriodDTO setDescription(String description) {
        this.description = description;
        return this;
    }
}
