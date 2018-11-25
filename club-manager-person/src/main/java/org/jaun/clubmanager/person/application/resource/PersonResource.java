package org.jaun.clubmanager.person.application.resource;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.person.domain.model.person.EmailAddress;
import org.jaun.clubmanager.person.domain.model.person.Gender;
import org.jaun.clubmanager.person.domain.model.person.Name;
import org.jaun.clubmanager.person.domain.model.person.Person;
import org.jaun.clubmanager.person.domain.model.person.PersonId;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistry;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistryRepository;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRequestId;
import org.jaun.clubmanager.person.domain.model.person.PersonRepository;
import org.jaun.clubmanager.person.domain.model.person.PhoneNumber;
import org.jaun.clubmanager.person.domain.model.person.StreetAddress;
import org.jaun.clubmanager.person.infra.projection.HazelcastPersonProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/")
public class PersonResource {

    @Autowired
    private PersonIdRegistryRepository personIdRegistryRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private HazelcastPersonProjection projection;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("persons")
    public Response searchContacts(@QueryParam("firstName") String firstName,
            @QueryParam("lastNameOrCompanyName") String lastNameOrCompanyName, @QueryParam("nameLine") String nameLine) {

        Collection<PersonDTO> personDTOS;
        if (nameLine != null) {
            personDTOS = projection.find(nameLine);
        } else {
            personDTOS = projection.find(firstName, lastNameOrCompanyName);
        }

        PersonsDTO personsDTO = new PersonsDTO();
        personsDTO.setPersons(personDTOS);

        return Response.ok(personDTOS).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("persons/{id}")
    public Response createContact(@HeaderParam("person-id-request-id") String personIdRequestIdAsString,
            @PathParam("id") String personIdAsString, @Valid CreatePersonDTO contactDTO) {

        PersonId personId = new PersonId(personIdAsString);
        Person person = personRepository.get(personId);
        Person personNew = PersonConverter.toPerson(personId, contactDTO);

        if (person == null) {

            if (personId.asInt() >= PersonIdRequestResource.START_ID_FROM) {
                // verify the person id was properly registered

                PersonIdRequestId personIdRequestId;
                try {
                    personIdRequestId = new PersonIdRequestId(UUID.fromString(personIdRequestIdAsString));
                } catch (NullPointerException | IllegalArgumentException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("person id is not valid: " + personIdRequestIdAsString)
                            .build();
                }
                PersonIdRegistry registry = personIdRegistryRepository.get(PersonIdRequestResource.PERSON_ID_REGISTRY_ID);

                if(registry == null) {
                    // if there is no registry yet then there is no registered person id of course
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("person id " + personIdAsString + " is not registered: " + personIdRequestIdAsString)
                            .build();
                }

                Optional<PersonId> registeredPersonId = registry.getPersonIdByRequestId(personIdRequestId);
                if (!registeredPersonId.isPresent()) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("person id " + personIdAsString + " is not registered: " + personIdRequestIdAsString)
                            .build();
                }
                if (!personId.equals(registeredPersonId.get())) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("person id " + personIdAsString + " was not registered with request: "
                                    + personIdRequestIdAsString)
                            .build();
                }
            }

            try {
                personRepository.save(personNew);
            } catch (ConcurrencyException e) {
                throw new IllegalStateException(e);
            }

            person = personNew;
        } else {

            person.changeBasicData(personNew.getName(), personNew.getBirthDate().orElse(null), personNew.getGender().orElse(null));
            person.changeStreetAddress(personNew.getStreetAddress());
            person.changeContactData(personNew.getEmailAddress().orElse(null), personNew.getPhoneNumber().orElse(null));

            try {
                personRepository.save(person);
            } catch (ConcurrencyException e) {
                throw new IllegalStateException(e);
            }
        }

        return Response.ok(person.getId().getValue()).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("persons/{id}/street-address")
    public Response changeStreetAddress(@PathParam("id") String contactId, StreetAddressDTO streetAddressDTO) {

        StreetAddress streetAddress = PersonConverter.toStreetAddress(streetAddressDTO);
        Person person = getForUpdate(new PersonId(contactId));
        person.changeStreetAddress(streetAddress);
        save(person);
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("persons/{id}/basic-data")
    public Response changeName(@PathParam("id") String contactId, BasicDataDTO basicDataDTO) {

        Name name = PersonConverter.toName(basicDataDTO.getName());
        Gender gender = PersonConverter.toGender(basicDataDTO.getGender());
        Person person = getForUpdate(new PersonId(contactId));
        person.changeBasicData(name, basicDataDTO.getBirthDate(), gender);
        save(person);
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("persons/{id}/contact-data")
    public Response changePhoneNumber(@PathParam("id") String contactId, ContactDataDTO contactDataDTO) {

        PhoneNumber phoneNumber = PersonConverter.toPhoneNumber(contactDataDTO.getPhoneNumber());
        EmailAddress emailAddress = PersonConverter.toEmailAddress(contactDataDTO.getEmailAddress());

        Person person = getForUpdate(new PersonId(contactId));
        person.changeContactData(emailAddress, phoneNumber);
        save(person);
        return Response.ok().build();
    }

    private Person getForUpdate(PersonId personId) {
        Person person = personRepository.get(personId);

        if (person == null) {
            throw new NotFoundException("could not find persons " + personId);
        }

        return person;
    }

    private void save(Person person) {
        try {
            personRepository.save(person);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("persons/{id}")
    public Response getContact(@PathParam("id") String contactId) {

        PersonDTO personDTO = projection.get(new PersonId(contactId));
        if (personDTO == null) {
            throw new NotFoundException();
        }

        return Response.ok().entity(personDTO).build();
    }
}
