package org.jaun.clubmanager.invoice.infra.repository;

import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.invoice.domain.model.invoice.Invoice;
import org.jaun.clubmanager.invoice.domain.model.invoice.InvoiceId;
import org.jaun.clubmanager.invoice.domain.model.invoice.InvoiceRepository;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.InvoiceEvent;
import org.springframework.stereotype.Service;

@Service
public class InvoiceRepositoryImpl extends AbstractGenericRepository<Invoice, InvoiceId, InvoiceEvent> implements
        InvoiceRepository {

    @Override
    protected String getAggregateName() {
        return "invoice";
    }

    @Override
    protected Invoice toAggregate(EventStream<InvoiceEvent> eventStream) {
        return new Invoice(eventStream);
    }

    @Override
    protected Class<? extends InvoiceEvent> getClassByName(String name) {
        return InvoiceEventMapping.of(name).getEventClass();
    }

    @Override
    protected String getNameByEvent(InvoiceEvent event) {
        return InvoiceEventMapping.of(event).getEventType();
    }
}