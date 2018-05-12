package org.jaun.clubmanager.member.infra.projection;

import java.util.ArrayList;
import java.util.Collection;

import org.jaun.clubmanager.domain.model.commons.AbstractProjection;
import org.jaun.clubmanager.member.application.resource.ContactDTO;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactCreatedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactEventType;
import org.jaun.clubmanager.member.domain.model.contact.event.NameChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.msemys.esjc.EventStore;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

@Service
public class HazelcastContactProjection extends AbstractProjection {

    private IMap<String, ContactDTO> contactMap;

    public HazelcastContactProjection(@Autowired EventStore eventStore, @Autowired HazelcastInstance hazelcastInstance) {
        super(eventStore, "$ce-contact");

        registerMapping(ContactEventType.CONTACT_CREATED, (r) -> update(toObject(r, ContactCreatedEvent.class)));
        registerMapping(ContactEventType.NAME_CHANGED, (r) -> update(toObject(r, NameChangedEvent.class)));

        contactMap = hazelcastInstance.getMap("contacts");
    }

    protected void update(ContactCreatedEvent contactCreatedEvent) {

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setContactId(contactCreatedEvent.getContactId().getValue());
        contactDTO.setContactType(contactCreatedEvent.getContactType().name()); // TODO: shuld be converted
        contactMap.put(contactCreatedEvent.getContactId().getValue(), contactDTO);
    }

    protected void update(NameChangedEvent nameChangedEvent) {

        ContactDTO contactDTO = contactMap.get(nameChangedEvent.getContactId().getValue());
        contactDTO.setFirstName(nameChangedEvent.getName().getFirstName().orElse(null));
        contactDTO.setLastNameOrCompanyName(nameChangedEvent.getName().getLastNameOrCompanyName());

        contactMap.put(nameChangedEvent.getContactId().getValue(), contactDTO);
    }

    public Collection<ContactDTO> find(String firstName, String lastName) {

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        if (firstName != null) {
            andPredicates.add(Predicates.ilike("firstName", "%" + firstName + "%"));
        }
        if (lastName != null) {
            andPredicates.add(Predicates.ilike("lastName", "%" + lastName + "%"));
        }

        Predicate criteriaQuery = Predicates.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

        return contactMap.values(criteriaQuery);
    }
}
