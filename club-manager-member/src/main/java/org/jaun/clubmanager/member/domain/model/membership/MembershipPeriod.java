package org.jaun.clubmanager.member.domain.model.membership;

import com.google.common.collect.ImmutableList;
import org.jaun.clubmanager.domain.model.commons.Aggregate;

import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MembershipPeriod extends Aggregate<MembershipPeriodId> {

    private final MembershipPeriodId id;
    private final Period period;
    private final String name;
    private final String description;

    private List<SubscriptionDefinition> subscriptionDefinitions = new ArrayList();

    public MembershipPeriod(MembershipPeriodId id, Period period, String name, String description, Collection<SubscriptionDefinition> subscriptionDefinitions) {
        this.id = id;
        this.period = period;
        this.name = name;
        this.description = description;
        this.subscriptionDefinitions = ImmutableList.copyOf(subscriptionDefinitions);
    }

    public Collection<SubscriptionDefinition> getSubscriptionDefinitions() {
        return ImmutableList.copyOf(subscriptionDefinitions);
    }

    @Override
    public MembershipPeriodId getId() {
        return id;
    }

    public Period getPeriod() {
        return period;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
