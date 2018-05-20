package org.jaun.clubmanager.invoice.infra.repository;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import org.jaun.clubmanager.domain.model.commons.EventMapping;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.InvoiceCreatedEvent;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.InvoiceEvent;
import org.jaun.clubmanager.invoice.domain.model.invoice.event.PaymentBookedEvent;

public enum InvoiceEventMapping implements EventMapping {
    INVOICE_CREATED("InvoiceCreated", InvoiceCreatedEvent.class), //
    PAYMENT_BOOKED("PaymentBooked", PaymentBookedEvent.class);

    private final String name;
    private final Class<? extends InvoiceEvent> eventClass;

    InvoiceEventMapping(String name, Class<? extends InvoiceEvent> eventClass) {
        this.name = requireNonNull(name);
        this.eventClass = requireNonNull(eventClass);
    }

    @Override
    public Class<? extends InvoiceEvent> getEventClass() {
        return eventClass;
    }

    @Override
    public String getEventType() {
        return name;
    }

    public static InvoiceEventMapping of(String name) {
        return Stream.of(InvoiceEventMapping.values())
                .filter(m -> m.getEventType().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for name " + name));
    }

    public static InvoiceEventMapping of(InvoiceEvent event) {
        return Stream.of(InvoiceEventMapping.values())
                .filter(m -> m.getEventClass().equals(event.getClass()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mapping not found for event " + event));
    }
}
