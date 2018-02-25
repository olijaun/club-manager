package org.jaun.clubmanager.member.application;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import com.github.msemys.esjc.ExpectedVersion;
import org.jaun.clubmanager.member.domain.model.collaboration.CollaboratorService;
import org.jaun.clubmanager.member.domain.model.member.Member;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

@Service
public class MemberApplicationServiceBean {

    private CollaboratorService collaboratorService;

    @Autowired
    private MemberRepository memberRepository;

    public Member getMember(MemberId id) {
        return memberRepository.getMember(id);
    }

    public Collection<Member> getMembers() {


        EventStore eventStore = EventStoreBuilder.newBuilder()
                .singleNodeAddress("127.0.0.1", 1113)
                .userCredentials("admin", "changeit")
                .build();

        try {
            eventStore.appendToStream("oliver-1", ExpectedVersion.ANY, Arrays.asList(
                    EventData.newBuilder().type("bar").jsonData("{ a : 1 }").build(),
                    EventData.newBuilder().type("baz").jsonData("{ b : 2 }").build())
            ).thenAccept(r -> System.out.println(r.logPosition)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return Arrays.asList( //
                memberRepository.getMember(new MemberId("1")),  //
                memberRepository.getMember(new MemberId("2")),  //
                memberRepository.getMember(new MemberId("3")));
    }
}
