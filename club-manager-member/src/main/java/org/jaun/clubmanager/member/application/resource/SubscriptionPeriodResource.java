package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipType;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.*;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Currency;

@Path("/subscription-periods")
@DenyAll
public class SubscriptionPeriodResource {

    @Inject
    private MembershipTypeRepository membershipTypeRepository;

    @Inject
    private SubscriptionPeriodRepository subscriptionPeriodRepository;

    @Inject
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

        Money price = new Money(subscriptionTypeDTO.getAmount(), currency);

        SubscriptionPeriod period = getForUpdate(new SubscriptionPeriodId(subscriptionPeriodIdString));

        period.addSubscriptionType(new SubscriptionTypeId(membershipOptionIdAsString), membershipType.getId(),
                subscriptionTypeDTO.getName(), price, subscriptionTypeDTO.getMaxSubscribers());

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
    public Response getSubscriptionType(@PathParam("period-id") String subscriptionPeriodIdString,
                                        @PathParam("id") String subscriptionTypeIdAsString) {

        SubscriptionTypeDTO subscriptionTypeDTO =
                projection.getSubscriptionType(new SubscriptionPeriodId(subscriptionPeriodIdString),
                        new SubscriptionTypeId(subscriptionTypeIdAsString))
                        .orElseThrow(() -> new NotFoundException(
                                "could not find subscription for period " + subscriptionPeriodIdString + " and subscriptionType "
                                        + subscriptionTypeIdAsString));

        return Response.ok(subscriptionTypeDTO).build();
    }
}
