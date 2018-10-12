package org.jaun.clubmanager.person.infra.projection;

import java.util.ArrayList;
import java.util.Collection;

import org.jaun.clubmanager.person.application.resource.BasicDataDTO;
import org.jaun.clubmanager.person.application.resource.ContactDataDTO;
import org.jaun.clubmanager.person.application.resource.NameDTO;
import org.jaun.clubmanager.person.application.resource.PersonConverter;
import org.jaun.clubmanager.person.application.resource.PersonDTO;
import org.jaun.clubmanager.person.domain.model.person.EmailAddress;
import org.jaun.clubmanager.person.domain.model.person.PersonId;
import org.jaun.clubmanager.person.domain.model.person.PhoneNumber;
import org.jaun.clubmanager.person.domain.model.person.Sex;
import org.jaun.clubmanager.person.domain.model.person.event.BasicDataChangedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.ContactDataChangedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonCreatedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.StreetAddressChangedEvent;
import org.jaun.clubmanager.person.infra.repository.PersonEventMapping;
import org.jaun.clubmanager.domain.model.commons.AbstractPollingProjection;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

@Service
public class HazelcastPersonProjection extends AbstractPollingProjection {

    private IMap<String, PersonDTO> personMap;

    public HazelcastPersonProjection(@Autowired EventStoreClient eventStore, @Autowired HazelcastInstance hazelcastInstance) {
        super(eventStore, "$ce-person");

        registerMapping(PersonEventMapping.PERSON_CREATED, (v, r) -> update(toObject(r, PersonCreatedEvent.class)));
        registerMapping(PersonEventMapping.BASIC_DATA_CHANGED, (v, r) -> update(toObject(r, BasicDataChangedEvent.class)));
        registerMapping(PersonEventMapping.STREE_ADDRESS_CHANGED, (v, r) -> update(toObject(r, StreetAddressChangedEvent.class)));
        registerMapping(PersonEventMapping.CONTACT_DATA_CHANGED, (v, r) -> update(toObject(r, ContactDataChangedEvent.class)));

        personMap = hazelcastInstance.getMap("persons");
    }

    protected void update(PersonCreatedEvent personCreatedEvent) {

        PersonDTO personDTO = new PersonDTO();
        personDTO.setId(personCreatedEvent.getPersonId().getValue());
        personDTO.setType(personCreatedEvent.getPersonType().name()); // TODO: should be converted
        personMap.put(personCreatedEvent.getPersonId().getValue(), personDTO);
    }

    protected void update(BasicDataChangedEvent basicDataChangedEvent) {

        PersonDTO personDTO = personMap.get(basicDataChangedEvent.getPersonId().getValue());

        BasicDataDTO basicDataDTO = new BasicDataDTO();

        NameDTO nameDTO = new NameDTO();
        nameDTO.setLastNameOrCompanyName(basicDataChangedEvent.getName().getLastNameOrCompanyName());
        nameDTO.setFirstName(basicDataChangedEvent.getName().getFirstName().orElse(null));

        basicDataDTO.setName(nameDTO);
        basicDataDTO.setBirthDate(basicDataChangedEvent.getBirthDate().orElse(null));
        basicDataDTO.setSex(basicDataChangedEvent.getSex().map(Sex::name).orElse(null));

        personDTO.setBasicData(basicDataDTO);

        personMap.put(basicDataChangedEvent.getPersonId().getValue(), personDTO);
    }

    protected void update(StreetAddressChangedEvent streetAddressChangedEvent) {

        PersonDTO personDTO = personMap.get(streetAddressChangedEvent.getPersonId().getValue());
        personDTO.setStreetAddress(streetAddressChangedEvent.getStreetAddress().map(PersonConverter::toAddressDTO).orElse(null));

        personMap.put(streetAddressChangedEvent.getPersonId().getValue(), personDTO);
    }

    protected void update(ContactDataChangedEvent contactDataChangedEvent) {

        PersonDTO personDTO = personMap.get(contactDataChangedEvent.getPersonId().getValue());

        ContactDataDTO contactDataDTO = new ContactDataDTO();

        contactDataDTO.setPhoneNumber(contactDataChangedEvent.getPhoneNumber().map(PhoneNumber::getValue).orElse(null));
        contactDataDTO.setEmailAddress(contactDataChangedEvent.getEmailAddress().map(EmailAddress::getValue).orElse(null));

        personDTO.setContactData(contactDataDTO);

        personMap.put(contactDataChangedEvent.getPersonId().getValue(), personDTO);
    }

    public PersonDTO get(PersonId personId) {
        return personMap.get(personId.getValue());
    }

    public Collection<PersonDTO> find(String firstName, String lastName) {

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        if (firstName != null) {
            andPredicates.add(Predicates.ilike("basicData.name.firstName", "%" + firstName + "%"));
        }
        if (lastName != null) {
            andPredicates.add(Predicates.ilike("basicData.name.lastNameOrCompanyName", "%" + lastName + "%"));
        }

        Predicate criteriaQuery = Predicates.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

        return personMap.values(criteriaQuery);
    }

    public Collection<PersonDTO> find(String nameLine) {

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        if (nameLine != null) {
            andPredicates.add(Predicates.ilike("basicData.name.firstName", "%" + nameLine + "%"));
        }
        if (nameLine != null) {
            andPredicates.add(Predicates.ilike("basicData.name.lastNameOrCompanyName", "%" + nameLine + "%"));
        }

        Predicate criteriaQuery = Predicates.or(andPredicates.toArray(new Predicate[andPredicates.size()]));

        return personMap.values(criteriaQuery);
    }
}
