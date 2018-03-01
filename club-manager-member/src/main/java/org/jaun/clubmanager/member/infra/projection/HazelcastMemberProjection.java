package org.jaun.clubmanager.member.infra.projection;

import com.github.msemys.esjc.*;
import com.google.gson.Gson;
import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.EventType;
import org.jaun.clubmanager.member.application.resource.MemberDTO;
import org.jaun.clubmanager.member.domain.model.member.event.MemberCreatedEvent;
import org.jaun.clubmanager.member.domain.model.member.event.MemberEventType;
import org.jaun.clubmanager.member.domain.model.member.event.NameChangedEvent;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

@Service
public class HazelcastMemberProjection {

    private final IMap<String, MemberDTO> members;

    private Gson gson = new Gson();

    CatchUpSubscriptionListener listener = new CatchUpSubscriptionListener() {

        public void onLiveProcessingStarted(CatchUpSubscription subscription) {
            System.out.println("Live processing started!");
        }

        public void onEvent(CatchUpSubscription subscription, ResolvedEvent event) {
            DomainEvent domainEvent = toObject(event);
            update(domainEvent);
        }

        public void onClose(CatchUpSubscription subscription, SubscriptionDropReason reason, Exception exception) {
            System.out.println("Subscription closed: " + reason);
            exception.printStackTrace();
        }
    };

    private void update(DomainEvent event) {
        if (event.getEventType().is(MemberEventType.MEMBER_CREATED)) {

            MemberCreatedEvent memberCreatedEvent = (MemberCreatedEvent) event;

            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setMemberId(memberCreatedEvent.getMemberId().getValue());

            members.put(memberCreatedEvent.getMemberId().getValue(), memberDTO);

        } else if (event.getEventType().is(MemberEventType.NAME_CHANGED)) {

            NameChangedEvent nameChangedEvent = (NameChangedEvent) event;

            MemberDTO memberDTO = members.get(nameChangedEvent.getMemberId().getValue());
            memberDTO.setFirstName(nameChangedEvent.getFirstName());
            memberDTO.setLastName(nameChangedEvent.getLastName());
            members.flush();

            members.put(nameChangedEvent.getMemberId().getValue(), memberDTO);
        }
    }

    public HazelcastMemberProjection() {
        Config config = new Config();

        NetworkConfig network = config.getNetworkConfig();
        //network.setPort(PORT_NUMBER);

        JoinConfig join = network.getJoin();
        join.getTcpIpConfig().setEnabled(false);
        join.getAwsConfig().setEnabled(false);
        join.getMulticastConfig().setEnabled(false);

        HazelcastInstance h = Hazelcast.newHazelcastInstance(config);
        members = h.getMap("members");
    }

    public void startSubscription() {

        EventStore eventStore = EventStoreBuilder.newBuilder()
                .singleNodeAddress("127.0.0.1", 1113)
                .userCredentials("admin", "changeit")
                .build();

        CatchUpSubscriptionSettings settings = CatchUpSubscriptionSettings.newBuilder()
                .resolveLinkTos(true).build();

        CatchUpSubscription catchupSubscription = eventStore.subscribeToStreamFrom("$ce-member", null, settings, listener);

        //eventStore.subscribeToAll()
    }

    public Collection<MemberDTO> find(String firstName, String lastName) {

        //Predicate sqlQuery = new SqlPredicate("active AND age BETWEEN 18 AND 21)");

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        if (firstName != null) {
            andPredicates.add(Predicates.equal("firstName", firstName));
        }
        if (lastName != null) {
            andPredicates.add(Predicates.equal("lastName", lastName));
        }

        Predicate criteriaQuery = Predicates.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

//        Collection<User> result1 = users.values(sqlQuery);
        Collection<MemberDTO> result2 = members.values(criteriaQuery);
//
//        System.out.println(result1);
//        System.out.println(result2);
        return result2;
    }

    private DomainEvent toObject(ResolvedEvent resolvedEvent) {

        try {
            return gson.fromJson(new String(resolvedEvent.event.data, "UTF-8"), getEventClass(() -> resolvedEvent.event.eventType));
            // getEventClass(resolvedEvent.event.eventType));
        } catch (RuntimeException | UnsupportedEncodingException e) {
            throw new IllegalStateException("could not deserialize event string to object: " + resolvedEvent.event.eventType, e);
        }
    }

    protected Class<? extends DomainEvent> getEventClass(EventType evenType) {
        return Stream.of(MemberEventType.values())
                .filter(et -> et.getName().equals(evenType.getName()))
                .map(MemberEventType::getEventClass)
                .findFirst()
                .get();
    }

}
