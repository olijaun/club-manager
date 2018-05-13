package org.jaun.clubmanager.member.application.resource;

import java.util.Collection;
import java.util.Collections;
import java.util.Currency;

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
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriod;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodRepository;
import org.jaun.clubmanager.member.domain.model.membership.MembershipRepository;
import org.jaun.clubmanager.member.domain.model.membership.MembershipType;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeRepository;
import org.jaun.clubmanager.member.domain.model.membership.SubscriptionOptionId;
import org.jaun.clubmanager.member.infra.projection.HazelcastMembershipProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/")
public class MemberResource {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private MembershipPeriodRepository membershipPeriodRepository;

    @Autowired
    private HazelcastMembershipProjection projection;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("membership-types")
    public Response getMembershipTypes() {
        // TODO: dto
        return Response.ok().entity(membershipTypeRepository.getAll()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("membership-periods")
    public Response createMembershipPeriods(MembershipPeriodDTO membershipPeriodDTO) {

        MembershipPeriod period = MembershipConverter.toMembershipPeriod(membershipPeriodDTO);

        try {
            membershipPeriodRepository.save(period);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(period.getId().getValue()).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("membership-periods/{id}/subscription-options")
    public Response addSubscriptionOption(@PathParam("id") String membershipPeriodIdString, SubscriptionOptionDTO defDTO) {

        MembershipPeriod period = membershipPeriodRepository.get(new MembershipPeriodId(membershipPeriodIdString));

        if (period == null) {
            throw new NotFoundException(membershipPeriodIdString);
        }

        MembershipType membershipType = membershipTypeRepository.get(new MembershipTypeId(defDTO.getMembershipTypeId()));

        if (membershipType == null) {
            throw new BadRequestException("membership type does not exist: " + defDTO.getMembershipTypeId());
        }

        Currency currency = MembershipConverter.toCurrency(defDTO.getCurrency());

        period.addSubscriptionOption(SubscriptionOptionId.random(SubscriptionOptionId::new), membershipType.getId(),
                defDTO.getName(), defDTO.getAmount(), currency, defDTO.getMaxSubscribers());

        try {
            membershipPeriodRepository.save(period);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(period.getId().getValue()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("membership-periods/{id}/subscription-options")
    public Response getSubscriptionOptions(@PathParam("id") String membershipPeriodIdString) {

        Collection<SubscriptionOptionDTO> allSubscriptionOptionsForPeriods =
                projection.getAllSubscriptionOptionsForPeriods(new MembershipPeriodId(membershipPeriodIdString));

        return Response.ok(allSubscriptionOptionsForPeriods).build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("memberships")
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
            membership = new Membership(membershipId, period,
                    new SubscriptionOptionId(createMembershipDTO.getSubscriptionOptionId()),
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
    @Path("memberships")
    public Response getMembership(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName,
            @QueryParam("membershipPeriodId") String membershipPeriodIdAsString) {

        MembershipPeriodId membershipPeriodId =
                membershipPeriodIdAsString == null ? null : new MembershipPeriodId(membershipPeriodIdAsString);

        Collection<MembershipViewDTO> view = projection.find(firstName, lastName, membershipPeriodId);

        return Response.ok(view).build(); //.entity(membershipPeriodRepository.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("memberships/{id}")
    public Response getMembership(@PathParam("id") String membershipId) {

        MembershipViewDTO view = projection.getById(new MembershipId(membershipId));

        return Response.ok(view).build(); //.entity(membershipPeriodRepository.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("membership-periods/{id}")
    public Response getMembershipPeriod(@PathParam("id") String membershipPeriodId) {

        MembershipPeriod p = membershipPeriodRepository.get(new MembershipPeriodId(membershipPeriodId));

        return Response.ok(p).build(); //.entity(membershipPeriodRepository.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("membership-periods")
    public Response getMembershipPeriods() {

        Collection<MembershipPeriodDTO> allMembershipPeriods = projection.getAllMembershipPeriods();

        return Response.ok(allMembershipPeriods).build(); //.entity(membershipPeriodRepository.getAll()).build();
    }
}
