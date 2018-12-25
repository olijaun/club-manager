package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipType;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/membership-types")
public class MembershipTypeResource {

    @Inject
    private MembershipTypeRepository membershipTypeRepository;

    @Inject
    private HazelcastMemberProjection projection;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response createMembershipType(@PathParam("id") String membershipTypeIdAsString,
            CreateMembershipTypeDTO createMembershipTypeDTO) {

        MembershipTypeId membershipTypeId = new MembershipTypeId(membershipTypeIdAsString);

        MembershipType membershipType = membershipTypeRepository.get(membershipTypeId);

        if (membershipType == null) {
            membershipType = new MembershipType(membershipTypeId, createMembershipTypeDTO.getName(),
                    createMembershipTypeDTO.getDescription());
        }

        try {
            membershipTypeRepository.save(membershipType);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Response.noContent().build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getMembershipTypes() {
        Collection<MembershipTypeDTO> membershipTypeDTOS = projection.getAllMembershipTypes();

        MembershipTypesDTO typesDTO = new MembershipTypesDTO();
        typesDTO.setMembershipTypes(membershipTypeDTOS);

        return Response.ok(typesDTO).build();
    }
}
