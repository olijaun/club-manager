package org.jaun.clubmanager.member.infra.repository;

import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriod;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodRepository;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionPeriodRepositoryImpl extends
        AbstractGenericRepository<SubscriptionPeriod, SubscriptionPeriodId, SubscriptionPeriodEvent> implements
        SubscriptionPeriodRepository {

    public SubscriptionPeriodRepositoryImpl(@Autowired EventStoreClient eventStoreClient) {
        super(eventStoreClient);
    }

    @Override
    protected String getAggregateName() {
        return "subscriptionperiod";
    }

    @Override
    protected SubscriptionPeriod toAggregate(EventStream<SubscriptionPeriodEvent> eventStream) {
        return new SubscriptionPeriod(eventStream);
    }

    @Override
    protected Class<? extends SubscriptionPeriodEvent> getClassByName(EventType name) {
        return SubscriptionPeriodEventMapping.of(name).getEventClass();
    }

    @Override
    protected EventType getNameByEvent(SubscriptionPeriodEvent event) {
        return SubscriptionPeriodEventMapping.of(event).getEventType();
    }
}
