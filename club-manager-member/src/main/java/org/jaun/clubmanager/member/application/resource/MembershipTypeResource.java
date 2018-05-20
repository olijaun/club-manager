package org.jaun.clubmanager.member.application.resource;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipType;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/membership-types")
public class MembershipTypeResource {

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private HazelcastMemberProjection projection;

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response createMembershipType(@PathParam("id") String membershipTypeIdAsString,
            CreateMembershipTypeDTO createMembershipTypeDTO) {

        MembershipType membershipType =
                new MembershipType(new MembershipTypeId(membershipTypeIdAsString), createMembershipTypeDTO.getName(),
                        createMembershipTypeDTO.getDescription());

        try {
            membershipTypeRepository.save(membershipType);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
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
