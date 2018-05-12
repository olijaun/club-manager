package org.jaun.clubmanager.member.infra.repository;

import java.util.stream.Stream;

import org.jaun.clubmanager.domain.model.commons.AbstractGenericRepository;
import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.domain.model.commons.EventType;
import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.ContactRepository;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactEventType;
import org.springframework.stereotype.Service;

@Service
public class ContactRepositoryImpl extends AbstractGenericRepository<Contact, ContactId> implements ContactRepository {

    @Override
    protected String getAggregateName() {
        return "contact";
    }

    @Override
    protected Contact toAggregate(EventStream<Contact> eventStream) {
        return new Contact(eventStream);
    }

    @Override
    protected Class<? extends DomainEvent> getEventClass(EventType evenType) {
        return Stream.of(ContactEventType.values())
                .filter(et -> et.getName().equals(evenType.getName()))
                .map(ContactEventType::getEventClass)
                .findFirst()
                .get();
    }
}
