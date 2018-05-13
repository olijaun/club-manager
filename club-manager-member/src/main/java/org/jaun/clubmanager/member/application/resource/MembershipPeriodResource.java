package org.jaun.clubmanager.member.application.resource;

import java.util.Collection;
import java.util.Currency;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.membership.MembershipRepository;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriod;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodRepository;
import org.jaun.clubmanager.member.domain.model.membershipperiod.SubscriptionOptionId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipType;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.infra.projection.HazelcastMembershipProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/membership-periods")
public class MembershipPeriodResource {

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private MembershipPeriodRepository membershipPeriodRepository;

    @Autowired
    private HazelcastMembershipProjection projection;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Response createMembershipPeriods(@PathParam("id") String id, MembershipPeriodDTO membershipPeriodDTO) {

        MembershipPeriod period = MembershipConverter.toMembershipPeriod(new MembershipPeriodId(id), membershipPeriodDTO);

        try {
            membershipPeriodRepository.save(period);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(period.getId().getValue()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getMembershipPeriod(@PathParam("id") String membershipPeriodId) {

        MembershipPeriodDTO p = projection.getById(new MembershipPeriodId(membershipPeriodId));

        return Response.ok(p).build(); //.entity(membershipPeriodRepository.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getMembershipPeriods() {

        Collection<MembershipPeriodDTO> allMembershipPeriods = projection.getAllMembershipPeriods();

        MembershipPeriodsDTO periodsDTO = new MembershipPeriodsDTO();
        periodsDTO.setMembershipPeriods(allMembershipPeriods);

        return Response.ok(periodsDTO).build(); //.entity(membershipPeriodRepository.getAll()).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{period-id}/subscription-options/{id}")
    public Response addSubscriptionOption(@PathParam("period-id") String membershipPeriodIdString,
            @PathParam("id") String subscriptionOptionIdAsString, CreateSubscriptionOptionDTO subscriptionOption) {

        MembershipType membershipType = getMembershipType(new MembershipTypeId(subscriptionOption.getMembershipTypeId()));
        Currency currency = MembershipConverter.toCurrency(subscriptionOption.getCurrency());

        MembershipPeriod period = getForUpdate(new MembershipPeriodId(membershipPeriodIdString));

        period.addSubscriptionOption(new SubscriptionOptionId(subscriptionOptionIdAsString), membershipType.getId(),
                subscriptionOption.getName(), subscriptionOption.getAmount(), currency, subscriptionOption.getMaxSubscribers());

        try {
            membershipPeriodRepository.save(period);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(period.getId().getValue()).build();
    }

    private MembershipPeriod getForUpdate(MembershipPeriodId membershipPeriodId) {
        MembershipPeriod period = membershipPeriodRepository.get(membershipPeriodId);

        if (period == null) {
            throw new NotFoundException(membershipPeriodId.getValue());
        }
        return period;
    }

    private MembershipType getMembershipType(MembershipTypeId membershipTypeId) {
        MembershipType membershipType = membershipTypeRepository.get(membershipTypeId);

        if (membershipType == null) {
            throw new BadRequestException("membership type does not exist: " + membershipTypeId);
        }
        return membershipType;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{period-id}/subscription-options")
    public Response getSubscriptionOptions(@PathParam("period-id") String membershipPeriodIdString) {

        Collection<SubscriptionOptionDTO> allSubscriptionOptionsForPeriods =
                projection.getAllSubscriptionOptionsForPeriods(new MembershipPeriodId(membershipPeriodIdString));

        SubscriptionOptionsDTO optionsDTO = new SubscriptionOptionsDTO();
        optionsDTO.setSubscriptionOptions(allSubscriptionOptionsForPeriods);

        return Response.ok(optionsDTO).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{period-id}/subscription-options/{id}")
    public Response getSubscriptionOption(@PathParam("period-id") String membershipPeriodIdString,
            @PathParam("id") String subscriptionOptionIdAsString) {

        SubscriptionOptionDTO subscriptionOptionDTO = projection.get(new MembershipPeriodId(membershipPeriodIdString),
                new SubscriptionOptionId(subscriptionOptionIdAsString));

        if (subscriptionOptionDTO == null) {
            throw new NotFoundException(
                    "could not find subscription for period " + membershipPeriodIdString + " and subscriptionOption "
                    + subscriptionOptionIdAsString);
        }

        return Response.ok(subscriptionOptionDTO).build();
    }
}
