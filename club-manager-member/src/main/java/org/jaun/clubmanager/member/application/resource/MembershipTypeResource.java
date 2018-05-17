package org.jaun.clubmanager.member.application.resource;

import java.util.Collection;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipType;
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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getMembershipTypes() {
        Collection<MembershipType> membershipTypes = membershipTypeRepository.getAll();

        Collection<MembershipTypeDTO> membershipTypeDTOS =
                membershipTypes.stream().map(MembershipTypeConverter::toMembershipTypeDTO).collect(Collectors.toSet());

        MembershipTypesDTO typesDTO = new MembershipTypesDTO();
        typesDTO.setMembershipTypes(membershipTypeDTOS);

        return Response.ok(typesDTO).build();
    }
}
