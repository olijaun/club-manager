package org.jaun.clubmanager.member.infra.projection;

import java.util.ArrayList;
import java.util.Collection;

import org.jaun.clubmanager.domain.model.commons.AbstractProjection;
import org.jaun.clubmanager.member.application.resource.ContactConverter;
import org.jaun.clubmanager.member.application.resource.ContactDTO;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.EmailAddress;
import org.jaun.clubmanager.member.domain.model.contact.PhoneNumber;
import org.jaun.clubmanager.member.domain.model.contact.Sex;
import org.jaun.clubmanager.member.domain.model.contact.event.BirthDateChangedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactCreatedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactEventType;
import org.jaun.clubmanager.member.domain.model.contact.event.EmailAddressChangedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.NameChangedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.PhoneNumberChangedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.SexChangedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.StreetAddressChangedEvent;
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
        registerMapping(ContactEventType.ADDRESS_CHANGED, (r) -> update(toObject(r, StreetAddressChangedEvent.class)));
        registerMapping(ContactEventType.EMAIL_CHANGED, (r) -> update(toObject(r, EmailAddressChangedEvent.class)));
        registerMapping(ContactEventType.PHONE_CHANGED, (r) -> update(toObject(r, PhoneNumberChangedEvent.class)));
        registerMapping(ContactEventType.BIRTHDATE_CHANGED, (r) -> update(toObject(r, BirthDateChangedEvent.class)));
        registerMapping(ContactEventType.SEX_CHANGED, (r) -> update(toObject(r, SexChangedEvent.class)));

        contactMap = hazelcastInstance.getMap("contacts");
    }

    protected void update(ContactCreatedEvent contactCreatedEvent) {

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setContactId(contactCreatedEvent.getContactId().getValue());
        contactDTO.setContactType(contactCreatedEvent.getContactType().name()); // TODO: should be converted
        contactMap.put(contactCreatedEvent.getContactId().getValue(), contactDTO);
    }

    protected void update(NameChangedEvent nameChangedEvent) {

        ContactDTO contactDTO = contactMap.get(nameChangedEvent.getContactId().getValue());
        contactDTO.setFirstName(nameChangedEvent.getName().getFirstName().orElse(null));
        contactDTO.setLastNameOrCompanyName(nameChangedEvent.getName().getLastNameOrCompanyName());

        contactMap.put(nameChangedEvent.getContactId().getValue(), contactDTO);
    }

    protected void update(StreetAddressChangedEvent streetAddressChangedEvent) {

        ContactDTO contactDTO = contactMap.get(streetAddressChangedEvent.getContactId().getValue());
        contactDTO.setStreetAddress(
                streetAddressChangedEvent.getStreetAddress().map(ContactConverter::toAddressDTO).orElse(null));

        contactMap.put(streetAddressChangedEvent.getContactId().getValue(), contactDTO);
    }

    protected void update(PhoneNumberChangedEvent phoneNumberChangedEvent) {

        ContactDTO contactDTO = contactMap.get(phoneNumberChangedEvent.getContactId().getValue());
        contactDTO.setPhoneNumber(phoneNumberChangedEvent.getPhoneNumber().map(PhoneNumber::getValue).orElse(null));

        contactMap.put(phoneNumberChangedEvent.getContactId().getValue(), contactDTO);
    }

    protected void update(EmailAddressChangedEvent emailAddressChangedEvent) {

        ContactDTO contactDTO = contactMap.get(emailAddressChangedEvent.getContactId().getValue());
        contactDTO.setEmailAddress(emailAddressChangedEvent.getEmailAddress().map(EmailAddress::getValue).orElse(null));

        contactMap.put(emailAddressChangedEvent.getContactId().getValue(), contactDTO);
    }

    protected void update(BirthDateChangedEvent birthDateChangedEvent) {

        ContactDTO contactDTO = contactMap.get(birthDateChangedEvent.getContactId().getValue());
        contactDTO.setBirthDate(birthDateChangedEvent.getBirthDate().orElse(null));

        contactMap.put(birthDateChangedEvent.getContactId().getValue(), contactDTO);
    }

    protected void update(SexChangedEvent sexChangedEvent) {

        ContactDTO contactDTO = contactMap.get(sexChangedEvent.getContactId().getValue());
        contactDTO.setSex(sexChangedEvent.getSex().map(Sex::name).orElse(null)); // TODO: map

        contactMap.put(sexChangedEvent.getContactId().getValue(), contactDTO);
    }

    public ContactDTO get(ContactId contactId) {
        return contactMap.get(contactId.getValue());
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
