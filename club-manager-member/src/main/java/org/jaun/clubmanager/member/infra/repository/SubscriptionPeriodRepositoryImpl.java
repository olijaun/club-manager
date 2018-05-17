package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriod;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodRepository;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodEvent;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionPeriodRepositoryImpl extends
        AbstractGenericRepository<SubscriptionPeriod, SubscriptionPeriodId, SubscriptionPeriodEvent> implements
        SubscriptionPeriodRepository {


    @Override
    protected String getAggregateName() {
        return "subscriptionperiod";
    }

    @Override
    protected SubscriptionPeriod toAggregate(EventStream<SubscriptionPeriodEvent> eventStream) {
        return new SubscriptionPeriod(eventStream);
    }

    @Override
    protected Class<? extends SubscriptionPeriodEvent> getClassByName(String name) {
        return SubscriptionPeriodEventMapping.of(name).getEventClass();
    }

    @Override
    protected String getNameByEvent(SubscriptionPeriodEvent event) {
        return SubscriptionPeriodEventMapping.of(event).getEventType();
    }
}
