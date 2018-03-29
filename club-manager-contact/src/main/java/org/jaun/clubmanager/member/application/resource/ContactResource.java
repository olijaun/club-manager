package org.jaun.clubmanager.member.application.resource;

import java.util.Collection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.ContactRepository;
import org.jaun.clubmanager.member.infra.projection.HazelcastContactProjection;
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
    public Response searchContacts(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName) {

        Collection<ContactDTO> contactDTOS = projection.find(firstName, lastName);

        return Response.ok(contactDTOS).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("contacts")
    public Response createContact(ContactDTO contactDTO) {

        Contact member = ContactConverter.toContact(contactDTO);

        try {
            contactRepository.save(member);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(member.getId().getValue()).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("contacts/{contacts-id}")
    public Response getContact(@PathParam("contacts-id") String memberId) {

        Contact contact = contactRepository.get(new ContactId(memberId));
        if (contact == null) {
            throw new NotFoundException();
        }

        ContactDTO contactDTO = ContactConverter.toContactDTO(contact);
        return Response.ok().entity(contactDTO).build();
    }
}
