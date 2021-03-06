package org.jaun.clubmanager.person.infra.projection;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import org.jaun.clubmanager.eventstore.AbstractMappingCatchUpSubscriptionListener;
import org.jaun.clubmanager.eventstore.Category;
import org.jaun.clubmanager.person.application.resource.*;
import org.jaun.clubmanager.person.domain.model.person.EmailAddress;
import org.jaun.clubmanager.person.domain.model.person.Gender;
import org.jaun.clubmanager.person.domain.model.person.PersonId;
import org.jaun.clubmanager.person.domain.model.person.PhoneNumber;
import org.jaun.clubmanager.person.domain.model.person.event.BasicDataChangedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.ContactDataChangedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonCreatedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.StreetAddressChangedEvent;
import org.jaun.clubmanager.person.infra.repository.PersonEventMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class HazelcastPersonProjection extends AbstractMappingCatchUpSubscriptionListener {

    private IMap<String, PersonDTO> personMap;

    public HazelcastPersonProjection(HazelcastInstance hazelcastInstance) {

        registerMapping(PersonEventMapping.PERSON_CREATED.getEventType(), (v, r) -> update(toObject(r, PersonCreatedEvent.class)));
        registerMapping(PersonEventMapping.BASIC_DATA_CHANGED.getEventType(), (v, r) -> update(toObject(r, BasicDataChangedEvent.class)));
        registerMapping(PersonEventMapping.STREE_ADDRESS_CHANGED.getEventType(), (v, r) -> update(toObject(r, StreetAddressChangedEvent.class)));
        registerMapping(PersonEventMapping.CONTACT_DATA_CHANGED.getEventType(), (v, r) -> update(toObject(r, ContactDataChangedEvent.class)));

        personMap = hazelcastInstance.getMap("persons");
    }

    public Collection<Category> categories() {
        return Collections.singleton(new Category("person"));
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
        basicDataDTO.setBirthDate(basicDataChangedEvent.getBirthDate().map(PersonConverter::toDateString).orElse(null));
        basicDataDTO.setGender(basicDataChangedEvent.getGender().map(Gender::name).orElse(null));

        personDTO.setBasicData(basicDataDTO);

        personMap.put(basicDataChangedEvent.getPersonId().getValue(), personDTO);
    }

    protected void update(StreetAddressChangedEvent streetAddressChangedEvent) {

        PersonDTO personDTO = personMap.get(streetAddressChangedEvent.getPersonId().getValue());
        personDTO.setStreetAddress(PersonConverter.toAddressDTO(streetAddressChangedEvent.getStreetAddress()));

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
