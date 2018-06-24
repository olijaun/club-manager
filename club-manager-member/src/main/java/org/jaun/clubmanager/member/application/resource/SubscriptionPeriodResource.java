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
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipType;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriod;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodRepository;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/subscription-periods")
public class SubscriptionPeriodResource {

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private SubscriptionPeriodRepository subscriptionPeriodRepository;

    @Autowired
    private HazelcastMemberProjection projection;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Response createSubscriptionPeriod(@PathParam("id") String id, CreateSubscriptionPeriodDTO subscriptionPeriodDTO) {

        SubscriptionPeriodId subscriptionPeriodId = new SubscriptionPeriodId(id);

        SubscriptionPeriod period = subscriptionPeriodRepository.get(subscriptionPeriodId);

        if (period == null) {
            period = MembershipConverter.toSubscriptionPeriod(subscriptionPeriodId, subscriptionPeriodDTO);
        }
        try {
            subscriptionPeriodRepository.save(period);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(period.getId().getValue()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getSubscriptionPeriod(@PathParam("id") String subscriptionPeriodId) {

        SubscriptionPeriodDTO p = projection.getById(new SubscriptionPeriodId(subscriptionPeriodId));

        return Response.ok(p).build(); //.entity(subscriptionPeriodRepository.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getSubscriptionPeriods() {

        Collection<SubscriptionPeriodDTO> allSubscriptionPeriods = projection.getAllSubscriptionPeriods();

        SubscriptionPeriodsDTO periodsDTO = new SubscriptionPeriodsDTO();
        periodsDTO.setSubscriptionPeriods(allSubscriptionPeriods);

        return Response.ok(periodsDTO).build(); //.entity(subscriptionPeriodRepository.getAll()).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{period-id}/types/{id}")
    public Response addMembershipOption(@PathParam("period-id") String subscriptionPeriodIdString,
            @PathParam("id") String membershipOptionIdAsString, CreateSubscriptionTypeDTO subscriptionTypeDTO) {

        MembershipType membershipType = getMembershipType(new MembershipTypeId(subscriptionTypeDTO.getMembershipTypeId()));
        Currency currency = MembershipConverter.toCurrency(subscriptionTypeDTO.getCurrency());

        SubscriptionPeriod period = getForUpdate(new SubscriptionPeriodId(subscriptionPeriodIdString));

        period.addMembershipOption(new SubscriptionTypeId(membershipOptionIdAsString), membershipType.getId(),
                subscriptionTypeDTO.getName(), subscriptionTypeDTO.getAmount(), currency, subscriptionTypeDTO.getMaxSubscribers());

        try {
            subscriptionPeriodRepository.save(period);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(period.getId().getValue()).build();
    }

    private SubscriptionPeriod getForUpdate(SubscriptionPeriodId subscriptionPeriodId) {
        SubscriptionPeriod period = subscriptionPeriodRepository.get(subscriptionPeriodId);

        if (period == null) {
            throw new NotFoundException(subscriptionPeriodId.getValue());
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
    @Path("/{period-id}/types")
    public Response getSubscriptionPeriodTypes(@PathParam("period-id") String subscriptionPeriodIdString) {

        Collection<SubscriptionTypeDTO> allSubscriptionTypesForPeriod =
                projection.getAllSubscriptionPeriodTypes(new SubscriptionPeriodId(subscriptionPeriodIdString));

        SubscriptionTypesDTO typesDTO = new SubscriptionTypesDTO();
        typesDTO.setSubscriptionTypes(allSubscriptionTypesForPeriod);

        return Response.ok(typesDTO).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{period-id}/types/{id}")
    public Response getMembershipOption(@PathParam("period-id") String subscriptionPeriodIdString,
            @PathParam("id") String subscriptionTypeIdAsString) {

        SubscriptionTypeDTO subscriptionTypeDTO = projection.get(new SubscriptionPeriodId(subscriptionPeriodIdString),
                new SubscriptionTypeId(subscriptionTypeIdAsString));

        if (subscriptionTypeDTO == null) {
            throw new NotFoundException(
                    "could not find subscription for period " + subscriptionPeriodIdString + " and subscriptionType "
                    + subscriptionTypeIdAsString);
        }

        return Response.ok(subscriptionTypeDTO).build();
    }
}
