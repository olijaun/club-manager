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
import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactService;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.MemberRepository;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriod;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodRepository;
import org.jaun.clubmanager.member.domain.model.membershipperiod.SubscriptionOptionId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.SubscriptionRequest;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.infra.projection.HazelcastMembershipProjection;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/memberships")
public class MembershipResource {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private MembershipPeriodRepository membershipPeriodRepository;

    @Autowired
    private HazelcastMembershipProjection projection;

    @Autowired
    private ContactService contactService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/")
    public Response addSubscription(CreateMembershipDTO createMembershipDTO) {

        MembershipPeriod period = getMembershipPeriod(new MembershipPeriodId(createMembershipDTO.getMembershipPeriodId()));
        MemberId memberId = new MemberId(createMembershipDTO.getSubscriberId());
        Member member = getOrCreateMember(memberId);

        SubscriptionOptionId subscriptionOptionId = new SubscriptionOptionId(createMembershipDTO.getSubscriptionOptionId());
        SubscriptionRequest subscriptionRequest = period.createSubscriptionRequest(subscriptionOptionId,
                Collections.emptyList());// TODO: support additional subscribers

        SubscriptionId subscriptionId = member.subscribe(subscriptionRequest);

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
            ContactId contactId = new ContactId(memberId.getValue());
            Contact contact = contactService.getContact(contactId);

            if (contact == null) {
                throw new BadRequestException("contact does not exist: " + contactId);
            }

            member = new Member(memberId, contact.getFirstName().orElse(null), contact.getLastNameOrCompanyName());
        }

        return member;
    }

    private MembershipPeriod getMembershipPeriod(MembershipPeriodId membershipPeriodId) {
        MembershipPeriod period = membershipPeriodRepository.get(membershipPeriodId);

        if (period == null) {
            throw new NotFoundException("could not find membership period " + membershipPeriodId.getValue());
        }
        return period;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/")
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

        MembershipViewDTO view = projection.getById(new SubscriptionId(membershipId));

        return Response.ok(view).build(); //.entity(membershipPeriodRepository.getAll()).build();
    }
}
