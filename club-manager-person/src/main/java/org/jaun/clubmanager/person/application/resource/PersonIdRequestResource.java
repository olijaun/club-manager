package org.jaun.clubmanager.person.application.resource;

import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistry;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistryId;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRegistryRepository;
import org.jaun.clubmanager.person.domain.model.person.PersonIdRequestId;

@Path("/person-id-requests")
public class PersonIdRequestResource {

    public static final PersonIdRegistryId PERSON_ID_REGISTRY_ID = new PersonIdRegistryId("default");
    public static final int START_ID_FROM = 1000;

    @Inject
    private PersonIdRegistryRepository personIdRegistryRepository;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{request-id}")
    public Response getPersonId(@PathParam("request-id") String requestIdAsString) {

        PersonIdRequestId personIdRequestId;
        try {
            personIdRequestId = new PersonIdRequestId(UUID.fromString(requestIdAsString));
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        PersonIdRegistry registry = personIdRegistryRepository.get(PERSON_ID_REGISTRY_ID);

        if (registry == null) {
            // if the registry does not exist then the id does not exist neither
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return registry.getPersonIdByRequestId(personIdRequestId)
                .map(id -> Response.ok(id.getValue()).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());

    }

    @PUT
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{request-id}")
    public Response requestPersonId(@PathParam("request-id") String requestIdAsString) {

        PersonIdRequestId personIdRequestId;
        try {
            personIdRequestId = new PersonIdRequestId(UUID.fromString(requestIdAsString));
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        PersonIdRegistry registry = personIdRegistryRepository.get(PERSON_ID_REGISTRY_ID);

        if (registry == null) {
            registry = new PersonIdRegistry(PERSON_ID_REGISTRY_ID, START_ID_FROM);
        }

        registry.requestId(personIdRequestId);

        try {
            personIdRegistryRepository.save(registry);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return registry.getPersonIdByRequestId(personIdRequestId)
                .map(r -> Response.ok(r.getValue()).build())
                .orElseGet(() -> Response.serverError().build());
    }
}
