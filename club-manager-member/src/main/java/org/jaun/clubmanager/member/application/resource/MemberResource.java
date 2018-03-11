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
import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.ContactRepository;
import org.jaun.clubmanager.member.domain.model.membership.Membership;
import org.jaun.clubmanager.member.domain.model.membership.MembershipId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriod;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodRepository;
import org.jaun.clubmanager.member.domain.model.membership.MembershipRepository;
import org.jaun.clubmanager.member.domain.model.membership.MembershipType;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeRepository;
import org.jaun.clubmanager.member.domain.model.membership.SubscriptionDefinitionId;
import org.jaun.clubmanager.member.infra.projection.HazelcastContactProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Path("/")
public class MemberResource {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private MembershipPeriodRepository membershipPeriodRepository;

    @Autowired
    private HazelcastContactProjection projection;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("contacts")
    public Response searchContacts(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName) {

//        Contact contact = contactRepository.get(new ContactId(memberId));
//        if(contact == null) {
//            throw new NotFoundException();
//        }
//
//        ContactDTO memberDTO = ContactConverter.toContactDTO(contact);
//
//        Collection<Contact> members = memberApplicationService.getMembers();
//
//        ContactsDTO membersDTO = ContactConverter.toMembersDTO(members);

        Collection<ContactDTO> contactDTOS = projection.find(firstName, lastName);

        return Response.ok(contactDTOS).build(); //.entity(membersDTO).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("contacts")
    public Response createContact(ContactDTO contactDTO) {

        Contact member = ContactConverter.toContact(contactDTO);

        try {
            contactRepository.save(member);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(member.getId().getValue()).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("contacts/{contacts-id}")
    public Response getContact(@PathParam("contacts-id") String memberId) {

        Contact contact = contactRepository.get(new ContactId(memberId));
        if (contact == null) {
            throw new NotFoundException();
        }

        ContactDTO contactDTO = ContactConverter.toContactDTO(contact);
        return Response.ok().entity(contactDTO).build();
    }

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

        MembershipPeriod period = ContactConverter.toMembershipPeriod(membershipPeriodDTO);

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
    @Path("membership-periods/{id}/definitions")
    public Response addSubscriptionDefinition(@PathParam("id") String membershipPeriodIdString, SubscriptionDefinitionDTO defDTO) {

        MembershipPeriod period = membershipPeriodRepository.get(new MembershipPeriodId(membershipPeriodIdString));

        if (period == null) {
            throw new NotFoundException(membershipPeriodIdString);
        }

        MembershipType membershipType = membershipTypeRepository.get(new MembershipTypeId(defDTO.getMembershipTypeId()));

        if (membershipType == null) {
            throw new BadRequestException("membership type does not exist: " + defDTO.getMembershipTypeId());
        }

        Currency currency = ContactConverter.toCurrency(defDTO.getCurrency());

        period.addDefinition(SubscriptionDefinitionId.random(SubscriptionDefinitionId::new), membershipType.getId(),
                defDTO.getName(), defDTO.getAmount(), currency, defDTO.getMaxSubscribers());

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
    @Path("memberships")
    public Response addSubscription(MembershipDTO membershipDTO) {

        MembershipPeriod period = membershipPeriodRepository.get(new MembershipPeriodId(membershipDTO.getMembershipPeriodId()));

        if (period == null) {
            throw new NotFoundException(membershipDTO.getMembershipPeriodId());
        }

        Contact contact = contactRepository.get(new ContactId(membershipDTO.getSubscriberId()));
        if (contact == null) {
            throw new BadRequestException("contact does not exist: " + membershipDTO.getSubscriberId());
        }

        MembershipId membershipId = new MembershipId(membershipDTO.getMembershipId());
        Membership membership = membershipRepository.get(membershipId);

        if (membership == null) {
            membership =
                    new Membership(membershipId, period, new SubscriptionDefinitionId(membershipDTO.getSubscriptionDefinitionId()),
                            contact.getId(), Collections.emptyList());
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
    @Path("membership-periods/{id}")
    public Response getMembershipPeriod(@PathParam("id") String membershipPeriodId) {

        MembershipPeriod p = membershipPeriodRepository.get(new MembershipPeriodId(membershipPeriodId));

        return Response.ok(p).build(); //.entity(membershipPeriodRepository.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("membership-periods")
    public Response getMembershipPeriods() {

        //MembershipPeriod p = membershipPeriodRepository.get()

        return Response.ok().build(); //.entity(membershipPeriodRepository.getAll()).build();
    }
}
