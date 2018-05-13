package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;

public class MembershipPeriodDTO implements Serializable {

    private String id;
    private String startDate;
    private String endDate;
    private String name;
    private String description;

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
