package org.jaun.clubmanager.member.application.resource;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.MemberRepository;
import org.jaun.clubmanager.member.domain.model.member.Subscription;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.domain.model.person.Person;
import org.jaun.clubmanager.member.domain.model.person.PersonId;
import org.jaun.clubmanager.member.domain.model.person.PersonService;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriod;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodRepository;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionRequest;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/members")
public class MemberResource {

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

    @GET
    @Produces({MediaType.APPLICATION_JSON, "text/csv"})
    @Path("/")
    public Response getMembers(@QueryParam("searchString") String searchString,
            @QueryParam("subscriptionPeriodId") String subscriptionPeriodIdAsString, @QueryParam("sortBy") String sortyBy,
            @QueryParam("sortAscending") String sortAscending) {

        if (StringUtils.isBlank(searchString)) {
            searchString = null;
        }

        if (StringUtils.isBlank(subscriptionPeriodIdAsString)) {
            subscriptionPeriodIdAsString = null;
        }

        boolean ascending = sortAscending == null || "true".equals(sortAscending);

        Collection<MemberDTO> memberDTOS = projection.searchMembers(searchString, subscriptionPeriodIdAsString, sortyBy, ascending);

        MembersDTO membersDTO = new MembersDTO();
        membersDTO.setSubscriptionPeriodIdFilter(subscriptionPeriodIdAsString);
        membersDTO.getMembers().addAll(memberDTOS);

        return Response.ok(membersDTO).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{member-id}")
    public Response getMember(@PathParam("member-id") String memberIdAsString) {

        MemberId memberId = new MemberId(memberIdAsString);

        return Response.ok(projection.getMember(memberId)).build();
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/{member-id}")
    public Response createOrUpdateMember(@PathParam("member-id") String memberIdAsString, CreateMemberDTO createMemberDTO) {

        MemberId memberId = new MemberId(memberIdAsString);

        Member member = getOrCreateMember(memberId);

        List<SubscriptionRequest> subscriptionRequests = createMemberDTO.getSubscriptions().stream().map(subscriptionDTO -> {

            SubscriptionId subscriptionId = new SubscriptionId(subscriptionDTO.getId());
            SubscriptionTypeId subscriptionTypeId = new SubscriptionTypeId(subscriptionDTO.getSubscriptionTypeId());
            SubscriptionPeriodId subscriptionPeriodId = new SubscriptionPeriodId(subscriptionDTO.getSubscriptionPeriodId());

            SubscriptionPeriod period = getSubscriptionPeriod(subscriptionPeriodId);
            SubscriptionRequest subscriptionRequest = period.createSubscriptionRequest(subscriptionId, subscriptionTypeId,
                    Collections.emptyList());// TODO: support additional subscribers

            return subscriptionRequest;
        }).collect(Collectors.toList());

        Collection<Subscription> removals = member.getSubscriptions().getRemovals(subscriptionRequests);

        // TODO: improve handling when subscription are modified etc.
        if (!removals.isEmpty()) {
            throw new BadRequestException("you must not remove existing subscriptions: " + removals.stream()
                    .map(Subscription::getId)
                    .map(SubscriptionId::getValue)
                    .collect(Collectors.toList()));
        }

        subscriptionRequests.stream().forEach(r -> member.subscribe(r));

        try {
            memberRepository.save(member);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok().build();
    }

    @POST
    @Consumes("text/csv")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("")
    public Response importMembers(InputStream inputStream) {

        MemberId memberId = new MemberId(memberIdAsString);

        Member member = getOrCreateMember(memberId);

        List<SubscriptionRequest> subscriptionRequests = createMemberDTO.getSubscriptions().stream().map(subscriptionDTO -> {

            SubscriptionId subscriptionId = new SubscriptionId(subscriptionDTO.getId());
            SubscriptionTypeId subscriptionTypeId = new SubscriptionTypeId(subscriptionDTO.getSubscriptionTypeId());
            SubscriptionPeriodId subscriptionPeriodId = new SubscriptionPeriodId(subscriptionDTO.getSubscriptionPeriodId());

            SubscriptionPeriod period = getSubscriptionPeriod(subscriptionPeriodId);
            SubscriptionRequest subscriptionRequest = period.createSubscriptionRequest(subscriptionId, subscriptionTypeId,
                    Collections.emptyList());// TODO: support additional subscribers

            return subscriptionRequest;
        }).collect(Collectors.toList());

        Collection<Subscription> removals = member.getSubscriptions().getRemovals(subscriptionRequests);

        // TODO: improve handling when subscription are modified etc.
        if (!removals.isEmpty()) {
            throw new BadRequestException("you must not remove existing subscriptions: " + removals.stream()
                    .map(Subscription::getId)
                    .map(SubscriptionId::getValue)
                    .collect(Collectors.toList()));
        }

        subscriptionRequests.stream().forEach(r -> member.subscribe(r));

        try {
            memberRepository.save(member);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok().build();
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
}
