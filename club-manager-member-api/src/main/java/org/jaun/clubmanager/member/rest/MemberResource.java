package org.jaun.clubmanager.member.rest;

import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberApplicationServiceBean;
import org.jaun.clubmanager.member.domain.model.member.MemberId;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Path("/members")
@Stateless
public class MemberResource {

    @EJB
    private MemberApplicationServiceBean memberApplicationService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("")
    public Response getMembers() {
        Collection<Member> members = memberApplicationService.getMembers();

        MembersDTO membersDTO = MemberConverter.toMembersDTO(members);

        return Response.ok().entity(membersDTO).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{member-id}")
    public Response getMember(@PathParam("member-id") String memberId) {
        Member member = memberApplicationService.getMember(new MemberId(memberId));

        MemberDTO memberDTO = MemberConverter.toMemberDTO(member);

        return Response.ok().entity(memberDTO).build();
    }
}
