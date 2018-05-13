package org.jaun.clubmanager.member.application.resource;

import java.util.Collection;
import java.util.Collections;

import javax.ws.rs.BadRequestException;
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
import org.jaun.clubmanager.member.domain.model.membership.MemberId;
import org.jaun.clubmanager.member.domain.model.membership.Membership;
import org.jaun.clubmanager.member.domain.model.membership.MembershipId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipRepository;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriod;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodRepository;
import org.jaun.clubmanager.member.domain.model.membershipperiod.SubscriptionOptionId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.infra.projection.HazelcastMembershipProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/memberships")
public class MembershipsResource {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private MembershipPeriodRepository membershipPeriodRepository;

    @Autowired
    private HazelcastMembershipProjection projection;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("")
    public Response addSubscription(CreateMembershipDTO createMembershipDTO) {

        MembershipPeriod period =
                membershipPeriodRepository.get(new MembershipPeriodId(createMembershipDTO.getMembershipPeriodId()));

        if (period == null) {
            throw new NotFoundException(createMembershipDTO.getMembershipPeriodId());
        }

        MemberDTO memberDTO = projection.getById(new MemberId(createMembershipDTO.getSubscriberId()));
        if (memberDTO == null) {
            throw new BadRequestException("member does not exist: " + createMembershipDTO.getSubscriberId());
        }

        MembershipId membershipId = new MembershipId(createMembershipDTO.getMembershipId());
        Membership membership = membershipRepository.get(membershipId);

        if (membership == null) {
            membership =
                    new Membership(membershipId, period, new SubscriptionOptionId(createMembershipDTO.getSubscriptionOptionId()),
                            new MemberId(memberDTO.getMemberId()), Collections.emptyList());
        }

        try {
            membershipRepository.save(membership);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(membership.getId().getValue()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("")
    public Response getMembership(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName,
            @QueryParam("membershipPeriodId") String membershipPeriodIdAsString) {

        MembershipPeriodId membershipPeriodId =
                membershipPeriodIdAsString == null ? null : new MembershipPeriodId(membershipPeriodIdAsString);

        Collection<MembershipViewDTO> view = projection.find(firstName, lastName, membershipPeriodId);

        return Response.ok(view).build(); //.entity(membershipPeriodRepository.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getMembership(@PathParam("id") String membershipId) {

        MembershipViewDTO view = projection.getById(new MembershipId(membershipId));

        return Response.ok(view).build(); //.entity(membershipPeriodRepository.getAll()).build();
    }
}
