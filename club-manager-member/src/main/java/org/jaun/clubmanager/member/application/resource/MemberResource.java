package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.MemberRepository;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriod;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodRepository;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeRepository;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Component
@Path("/")
public class MemberResource {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    @Autowired
    private MembershipPeriodRepository membershipPeriodRepository;

    @Autowired
    private HazelcastMemberProjection projection;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("members")
    public Response searchMembers(@QueryParam("firstName") String firstName, @QueryParam("lastName")String lastName) {

//        Member member = memberRepository.get(new MemberId(memberId));
//        if(member == null) {
//            throw new NotFoundException();
//        }
//
//        MemberDTO memberDTO = MemberConverter.toMemberDTO(member);
//
//        Collection<Member> members = memberApplicationService.getMembers();
//
//        MembersDTO membersDTO = MemberConverter.toMembersDTO(members);

        Collection<MemberDTO> memberDTOS = projection.find(firstName, lastName);

        return Response.ok(memberDTOS).build(); //.entity(membersDTO).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("members")
    public Response createMember(MemberDTO memberDTO) {

        Member member = MemberConverter.toMember(memberDTO);

        try {
            memberRepository.save(member);
        } catch (ConcurrencyException e) {
            throw new IllegalStateException(e);
        }

        return Response.ok(member.getId().getValue()).build();
    }


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("members/{member-id}")
    public Response getMember(@PathParam("member-id") String memberId) {

        Member member = memberRepository.get(new MemberId(memberId));
        if(member == null) {
            throw new NotFoundException();
        }

        MemberDTO memberDTO = MemberConverter.toMemberDTO(member);
        return Response.ok().entity(memberDTO).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("membership-types")
    public Response getMembershipTypes() {
        // TODO: dto
        return Response.ok().entity(membershipTypeRepository.getAll()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("membership-periods")
    public Response getMembershipPeriods() {
        // TODO: dto
        return Response.ok().entity(membershipPeriodRepository.getAll()).build();
    }
}
