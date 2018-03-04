package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.ContactRepository;
import org.jaun.clubmanager.member.domain.model.membership.*;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;
import java.util.Currency;
import java.util.Locale;

@Component
@Path("/")
public class MemberResource {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private MembershipPeriodRepository membershipPeriodRepository;

    @Autowired
    private HazelcastMemberProjection projection;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("members")
    public Response searchMembers(@QueryParam("firstName") String firstName, @QueryParam("lastName") String lastName) {

//        Contact contact = contactRepository.get(new ContactId(memberId));
//        if(contact == null) {
//            throw new NotFoundException();
//        }
//
//        MemberDTO memberDTO = ContactConverter.toContactDTO(contact);
//
//        Collection<Contact> members = memberApplicationService.getMembers();
//
//        MembersDTO membersDTO = ContactConverter.toMembersDTO(members);

        Collection<MemberDTO> memberDTOS = projection.find(firstName, lastName);

        return Response.ok(memberDTOS).build(); //.entity(membersDTO).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("members")
    public Response createMember(MemberDTO memberDTO) {

        Contact member = ContactConverter.toMember(memberDTO);

        try {
            contactRepository.save(member);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(member.getId().getValue()).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("members/{member-id}")
    public Response getMember(@PathParam("member-id") String memberId) {

        Contact contact = contactRepository.get(new ContactId(memberId));
        if (contact == null) {
            throw new NotFoundException();
        }

        MemberDTO memberDTO = ContactConverter.toContactDTO(contact);
        return Response.ok().entity(memberDTO).build();
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

        period.addDefinition(SubscriptionDefinitionId.random(SubscriptionDefinitionId::new), membershipType.getId(), defDTO.getName(), defDTO.getAmount(), currency, defDTO.getMaxSubscribers());

        try {
            membershipPeriodRepository.save(period);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(period.getId().getValue()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("membership-periods/{id}")
    public Response getMembershipPeriod(@PathParam("id")String membershipPeriodId) {

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
