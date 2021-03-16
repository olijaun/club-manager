package org.jaun.clubmanager.person.application.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
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

@Path("/")
public class PersonResource {

    @Inject
    private PersonIdRegistryRepository personIdRegistryRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private HazelcastPersonProjection projection;

    @GET
    @Path("readiness")
    public Response readiness() {
        return Response.ok().build();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON, "text/csv"})
    @Path("persons")
    public Response searchPersons(@QueryParam("firstName") String firstName,
            @QueryParam("lastNameOrCompanyName") String lastNameOrCompanyName, @QueryParam("nameLine") String nameLine) {

        Collection<PersonDTO> personDTOS;
        if (nameLine != null) {
            personDTOS = projection.find(nameLine);
        } else {
            personDTOS = projection.find(firstName, lastNameOrCompanyName);
        }

        PersonsDTO personsDTO = new PersonsDTO();
        personsDTO.setPersons(personDTOS);

        return Response.ok(personsDTO).build();
    }

    @POST
    @Consumes("text/csv")
    @Produces({MediaType.APPLICATION_JSON})
    @Path("persons")
    public Response importPersons(InputStream inputStream) {

        // reset the person id registry to the highest person id imported
        PersonIdRegistry registry = personIdRegistryRepository.get(PersonIdRequestResource.PERSON_ID_REGISTRY_ID);

        if (registry == null) {
            registry = new PersonIdRegistry(PersonIdRequestResource.PERSON_ID_REGISTRY_ID, PersonIdRequestResource.START_ID_FROM);

            try {
                personIdRegistryRepository.save(registry);
            } catch (ConcurrencyException e) {
                throw new IllegalStateException(e);
            }
        }

        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        CSVParser csvParser;
        try {
            csvParser = new CSVParser(reader, PersonCsvFormat.FORMAT.withNullString(""));
        } catch (IOException e) {
            return Response.serverError().build();
        }

        List<CSVRecord> records;

        try {
            records = csvParser.getRecords();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Optional<PersonId> highestPersonId = Optional.empty();

        PersonCsvImportResultDTO result = new PersonCsvImportResultDTO();
        try {

            // skip header because CSVParser seams to return it although i specified "withHeader"
            for (int i = 1; i < records.size(); i++) {

                CSVRecord r = records.get(i);

                PersonId personId = new PersonId(r.get(PersonCsvFormat.ID));
                Person person = personRepository.get(personId);

                if (person != null) {

                    result.getIgnoredIds().add(personId.getValue());

                } else {

                    try {
                        NameDTO nameDTO = new NameDTO();
                        nameDTO.setFirstName(r.get(PersonCsvFormat.FIRST_NAME));
                        nameDTO.setLastNameOrCompanyName(r.get(PersonCsvFormat.LAST_NAME));

                        BasicDataDTO basicDataDTO = new BasicDataDTO();
                        basicDataDTO.setBirthDate(r.get(PersonCsvFormat.BIRTH_DATE));
                        basicDataDTO.setGender(r.get(PersonCsvFormat.GENDER));
                        basicDataDTO.setName(nameDTO);

                        StreetAddressDTO streetAddressDTO = new StreetAddressDTO();
                        streetAddressDTO.setStreet(r.get(PersonCsvFormat.STREET));
                        streetAddressDTO.setHouseNumber(r.get(PersonCsvFormat.HOUSE_NUMBER));
                        streetAddressDTO.setZip(r.get(PersonCsvFormat.ZIP));
                        streetAddressDTO.setCity(r.get(PersonCsvFormat.CITY));
                        streetAddressDTO.setState(r.get(PersonCsvFormat.STATE));
                        streetAddressDTO.setIsoCountryCode(r.get(PersonCsvFormat.ISO_COUNTRY_CODE));

                        ContactDataDTO contactDataDTO = new ContactDataDTO();
                        contactDataDTO.setEmailAddress(r.get(PersonCsvFormat.EMAIL_ADDRESS));
                        contactDataDTO.setPhoneNumber(r.get(PersonCsvFormat.PHONE_NUMBER));

                        CreatePersonDTO createPersonDTO = new CreatePersonDTO();
                        createPersonDTO.setType(r.get(PersonCsvFormat.TYPE));

                        createPersonDTO.setBasicData(basicDataDTO);

                        if (contactDataDTO.getEmailAddress() != null && contactDataDTO.getPhoneNumber() != null) {
                            createPersonDTO.setContactData(contactDataDTO);
                        }

                        if (streetAddressDTO.getStreet() != null || streetAddressDTO.getHouseNumber() != null
                            || streetAddressDTO.getZip() != null || streetAddressDTO.getCity() != null
                            || streetAddressDTO.getState() != null) {

                            createPersonDTO.setStreetAddress(streetAddressDTO);
                        }

                        personRepository.save(PersonConverter.toPerson(personId, createPersonDTO));
                        result.getImportedIds().add(personId.getValue());

                        if (!highestPersonId.isPresent() || personId.asInt() > highestPersonId.get().asInt()) {
                            highestPersonId = Optional.of(personId);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.getFailed().add(new PersonCsvImportResultDTO.FailedImport(personId.getValue(), e.getMessage()));
                        //return Response.serverError().entity(result).build();
                    }
                }
            }

        } finally {
            highestPersonId.ifPresent(registry::continueAfter);

            try {
                personIdRegistryRepository.save(registry);
            } catch (ConcurrencyException e) {
                throw new IllegalStateException(e);
            }
        }
        return Response.ok(result).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces({MediaType.TEXT_PLAIN})
    @Path("persons/{id}")
    public Response createPerson(@HeaderParam("person-id-request-id") String personIdRequestIdAsString,
            @PathParam("id") String personIdAsString, @Valid CreatePersonDTO personDTO) {

        PersonId personId = new PersonId(personIdAsString);
        Person person = personRepository.get(personId);
        Person personNew = PersonConverter.toPerson(personId, personDTO);

        if (person == null) {

            if (personId.asInt() >= PersonIdRequestResource.START_ID_FROM) {
                // verify the person id was properly registered

                PersonIdRequestId personIdRequestId;
                try {
                    personIdRequestId = new PersonIdRequestId(UUID.fromString(personIdRequestIdAsString));
                } catch (NullPointerException | IllegalArgumentException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("person-id-request-id is not valid: " + personIdRequestIdAsString)
                            .build();
                }
                PersonIdRegistry registry = personIdRegistryRepository.get(PersonIdRequestResource.PERSON_ID_REGISTRY_ID);

                if (registry == null) {
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

            if(personNew.getStreetAddress().isPresent()) {
                person.changeStreetAddress(personNew.getStreetAddress().get());
            }

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
    public Response changeStreetAddress(@PathParam("id") String personId, StreetAddressDTO streetAddressDTO) {

        StreetAddress streetAddress = PersonConverter.toStreetAddress(streetAddressDTO);
        Person person = getForUpdate(new PersonId(personId));
        person.changeStreetAddress(streetAddress);
        save(person);
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("persons/{id}/basic-data")
    public Response changeName(@PathParam("id") String personId, BasicDataDTO basicDataDTO) {

        Name name = PersonConverter.toName(basicDataDTO.getName());
        Gender gender = PersonConverter.toGender(basicDataDTO.getGender());
        Person person = getForUpdate(new PersonId(personId));
        person.changeBasicData(name, PersonConverter.toLocalDate(basicDataDTO.getBirthDate()), gender);
        save(person);
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("persons/{id}/contact-data")
    public Response changePhoneNumber(@PathParam("id") String personId, ContactDataDTO contactDataDTO) {

        PhoneNumber phoneNumber = PersonConverter.toPhoneNumber(contactDataDTO.getPhoneNumber());
        EmailAddress emailAddress = PersonConverter.toEmailAddress(contactDataDTO.getEmailAddress());

        Person person = getForUpdate(new PersonId(personId));
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
    public Response getPerson(@PathParam("id") String personId) {

        PersonDTO personDTO = projection.get(new PersonId(personId));
        if (personDTO == null) {
            throw new NotFoundException();
        }

        return Response.ok().entity(personDTO).build();
    }
}
