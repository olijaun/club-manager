package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.domain.model.commons.ConcurrencyException;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.MemberRepository;
import org.jaun.clubmanager.member.infra.projection.HazelcastMemberProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Component
@Path("/members")
public class MemberResource {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HazelcastMemberProjection projection;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("")
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
    @Path("")
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
    @Path("{member-id}")
    public Response getMember(@PathParam("member-id") String memberId) {

        Member member = memberRepository.get(new MemberId(memberId));
        if(member == null) {
            throw new NotFoundException();
        }

        MemberDTO memberDTO = MemberConverter.toMemberDTO(member);
        return Response.ok().entity(memberDTO).build();
    }
}
