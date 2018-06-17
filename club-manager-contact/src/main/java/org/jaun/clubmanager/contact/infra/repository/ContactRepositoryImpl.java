package org.jaun.clubmanager.contact.infra.repository;

import org.jaun.clubmanager.contact.domain.model.contact.Contact;
import org.jaun.clubmanager.contact.domain.model.contact.ContactId;
import org.jaun.clubmanager.contact.domain.model.contact.ContactRepository;
import org.jaun.clubmanager.contact.domain.model.contact.event.ContactEvent;
import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.eventstore.EventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContactRepositoryImpl extends AbstractGenericRepository<Contact, ContactId, ContactEvent> implements
        ContactRepository {

    public ContactRepositoryImpl(@Autowired EventStoreClient eventStoreClient) {
        super(eventStoreClient);
    }

    @Override
    protected String getAggregateName() {
        return "contact";
    }

    @Override
    protected Contact toAggregate(EventStream<ContactEvent> eventStream) {
        return new Contact(eventStream);
    }

    @Override
    protected Class<? extends ContactEvent> getClassByName(EventType name) {
        return ContactEventMapping.of(name).getEventClass();
    }

    @Override
    protected EventType getNameByEvent(ContactEvent event) {
        return ContactEventMapping.of(event).getEventType();
    }
}
