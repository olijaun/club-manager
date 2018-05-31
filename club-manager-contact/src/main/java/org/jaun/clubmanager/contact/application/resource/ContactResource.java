package org.jaun.clubmanager.contact.application.resource;

import java.time.LocalDate;
import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jaun.clubmanager.contact.domain.model.contact.Contact;
import org.jaun.clubmanager.contact.domain.model.contact.ContactId;
import org.jaun.clubmanager.contact.domain.model.contact.ContactRepository;
import org.jaun.clubmanager.contact.domain.model.contact.EmailAddress;
import org.jaun.clubmanager.contact.domain.model.contact.Name;
import org.jaun.clubmanager.contact.domain.model.contact.PhoneNumber;
import org.jaun.clubmanager.contact.domain.model.contact.Sex;
import org.jaun.clubmanager.contact.domain.model.contact.StreetAddress;
import org.jaun.clubmanager.contact.infra.projection.HazelcastContactProjection;
import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/")
public class ContactResource {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private HazelcastContactProjection projection;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("contacts")
    public Response searchContacts(@QueryParam("firstName") String firstName,
            @QueryParam("lastNameOrCompanyName") String lastNameOrCompanyName, @QueryParam("nameLine") String nameLine) {

        Collection<ContactDTO> contactDTOS;
        if (nameLine != null) {
            contactDTOS = projection.find(nameLine);
        } else {
            contactDTOS = projection.find(firstName, lastNameOrCompanyName);
        }

        ContactsDTO contactsDTO = new ContactsDTO();
        contactsDTO.setContacts(contactDTOS);

        return Response.ok(contactDTOS).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("contacts/{id}")
    public Response createContact(@PathParam("id") String contactId, CreateContactDTO contactDTO) {

        Contact contact = ContactConverter.toContact(new ContactId(contactId), contactDTO);

        try {
            contactRepository.save(contact);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(contact.getId().getValue()).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("contacts/{id}/street-address")
    public Response changeStreetAddress(@PathParam("id") String contactId, StreetAddressDTO streetAddressDTO) {

        StreetAddress streetAddress = ContactConverter.toStreetAddress(streetAddressDTO);
        Contact contact = getForUpdate(new ContactId(contactId));
        contact.changeStreetAddress(streetAddress);
        save(contact);
        return Response.ok().build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("contacts/{id}/name")
    public Response changeName(@PathParam("id") String contactId, NameDTO nameDTO) {

        Name name = ContactConverter.toName(nameDTO);
        Contact contact = getForUpdate(new ContactId(contactId));
        contact.changeName(name);
        save(contact);
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("contacts/{id}/commands/change-sex")
    public Response changeSex(@PathParam("id") String contactId, @QueryParam("sex") String sexAsString) {

        Sex sex = ContactConverter.toSex(sexAsString);
        Contact contact = getForUpdate(new ContactId(contactId));
        contact.changeSex(sex);
        save(contact);
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("contacts/{id}/commands/change-birth-date")
    public Response changeBirthDate(@PathParam("id") String contactId, @QueryParam("birth-date") String birthDateAsString) {

        LocalDate birthDate = ContactConverter.toLocalDate(birthDateAsString);
        Contact contact = getForUpdate(new ContactId(contactId));
        contact.changeBirthdate(birthDate);
        save(contact);
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("contacts/{id}/commands/change-phone-number")
    public Response changePhoneNumber(@PathParam("id") String contactId, @QueryParam("phone-number") String phoneNumberAsString) {

        PhoneNumber phoneNumber = ContactConverter.toPhoneNumber(phoneNumberAsString);
        Contact contact = getForUpdate(new ContactId(contactId));
        contact.changePhoneNumber(phoneNumber);
        save(contact);
        return Response.ok().build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("contacts/{id}/commands/change-email-address")
    public Response changeEmailAddress(@PathParam("id") String contactId,
            @QueryParam("email-address") String emailAddressAsString) {

        EmailAddress emailAddress = ContactConverter.toEmailAddress(emailAddressAsString);
        Contact contact = getForUpdate(new ContactId(contactId));
        contact.changeEmailAddress(emailAddress);
        save(contact);
        return Response.ok().build();
    }

    private Contact getForUpdate(ContactId contactId) {
        Contact contact = contactRepository.get(contactId);

        if (contact == null) {
            throw new NotFoundException("could not find contact " + contactId);
        }

        return contact;
    }

    private void save(Contact contact) {
        try {
            contactRepository.save(contact);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("contacts/{id}")
    public Response getContact(@PathParam("id") String contactId) {

        ContactDTO contactDTO = projection.get(new ContactId(contactId));
        if (contactDTO == null) {
            throw new NotFoundException();
        }

        return Response.ok().entity(contactDTO).build();
    }
}
