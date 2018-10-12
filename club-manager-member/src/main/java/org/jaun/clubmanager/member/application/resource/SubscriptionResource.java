package org.jaun.clubmanager.member.application.resource;

import java.util.Collection;
import java.util.Collections;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.person.Person;
import org.jaun.clubmanager.member.domain.model.person.PersonId;
import org.jaun.clubmanager.member.domain.model.person.PersonService;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.MemberRepository;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriod;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodRepository;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionRequest;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/subscriptions")
public class SubscriptionResource {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private SubscriptionPeriodRepository subscriptionPeriodRepository;

    @Autowired
    private HazelcastMemberProjection projection;

    @Autowired
    private PersonService personService;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{id}")
    public Response addSubscription(@PathParam("id") String subscriptionIdAsString, CreateSubscriptionDTO createSubscriptionDTO) {

        MemberId memberId = new MemberId(createSubscriptionDTO.getSubscriberId());
        SubscriptionId subscriptionId = new SubscriptionId(subscriptionIdAsString);
        SubscriptionTypeId subscriptionTypeId = new SubscriptionTypeId(createSubscriptionDTO.getSubscriptionTypeId());
        SubscriptionPeriodId subscriptionPeriodId = new SubscriptionPeriodId(createSubscriptionDTO.getSubscriptionPeriodId());

        Member member = getOrCreateMember(memberId);

        SubscriptionPeriod period = getSubscriptionPeriod(subscriptionPeriodId);
        SubscriptionRequest subscriptionRequest = period.createSubscriptionRequest(subscriptionId, subscriptionTypeId,
                Collections.emptyList());// TODO: support additional subscribers

        member.subscribe(subscriptionRequest);

        try {
            memberRepository.save(member);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(subscriptionId.getValue()).build();
    }

    private Member getOrCreateMember(MemberId memberId) {

        Member member = memberRepository.get(memberId);
        if (member == null) {
            PersonId personId = new PersonId(memberId.getValue());
            Person person = personService.getPerson(personId);

            if (person == null) {
                throw new BadRequestException("person does not exist: " + personId);
            }

            member = new Member(memberId);
        }

        return member;
    }

    private SubscriptionPeriod getSubscriptionPeriod(SubscriptionPeriodId subscriptionPeriodId) {
        SubscriptionPeriod period = subscriptionPeriodRepository.get(subscriptionPeriodId);

        if (period == null) {
            throw new NotFoundException("could not find membership period " + subscriptionPeriodId.getValue());
        }
        return period;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
    public Response getSubscriptions(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName,
            @QueryParam("subscriptionPeriodId") String subscriptionPeriodIdAsString) {

        SubscriptionPeriodId subscriptionPeriodId =
                subscriptionPeriodIdAsString == null ? null : new SubscriptionPeriodId(subscriptionPeriodIdAsString);

        Collection<SubscriptionViewDTO> view = projection.find(firstName, lastName, subscriptionPeriodId, null);

        return Response.ok(view).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response getSubscriptions(@PathParam("id") String subscriptionId) {

        SubscriptionViewDTO view = projection.getById(new SubscriptionId(subscriptionId));

        return Response.ok(view).build();
    }
}
