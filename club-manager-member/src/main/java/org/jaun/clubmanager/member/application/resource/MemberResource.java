package org.jaun.clubmanager.member.application.resource;

import org.jaun.clubmanager.member.application.MemberApplicationServiceBean;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

@Component
@Path("/members")
public class MemberResource {

    @Autowired
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
