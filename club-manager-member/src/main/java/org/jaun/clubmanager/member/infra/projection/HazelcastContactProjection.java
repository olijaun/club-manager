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
import org.jaun.clubmanager.member.application.resource.ContactDTO;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactCreatedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactEventType;
import org.jaun.clubmanager.member.domain.model.contact.event.NameChangedEvent;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

@Service
public class HazelcastContactProjection {

    private final IMap<String, ContactDTO> contactMap;

    private Gson gson = new Gson();

    public HazelcastContactProjection() {
        Config config = new Config();

        NetworkConfig network = config.getNetworkConfig();
        //network.setPort(PORT_NUMBER);

        JoinConfig join = network.getJoin();
        join.getTcpIpConfig().setEnabled(false);
        join.getAwsConfig().setEnabled(false);
        join.getMulticastConfig().setEnabled(false);

        HazelcastInstance h = Hazelcast.newHazelcastInstance(config);
        contactMap = h.getMap("members");
    }

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
        if (event.getEventType().is(ContactEventType.CONTACT_CREATED)) {

            ContactCreatedEvent contactCreatedEvent = (ContactCreatedEvent) event;

            ContactDTO contactDTO = new ContactDTO();
            contactDTO.setContactId(contactCreatedEvent.getContactId().getValue());

            contactMap.put(contactCreatedEvent.getContactId().getValue(), contactDTO);

        } else if (event.getEventType().is(ContactEventType.NAME_CHANGED)) {

            NameChangedEvent nameChangedEvent = (NameChangedEvent) event;

            ContactDTO contactDTO = contactMap.get(nameChangedEvent.getContactId().getValue());
            contactDTO.setFirstName(nameChangedEvent.getFirstName());
            contactDTO.setLastName(nameChangedEvent.getLastName());

            contactMap.put(nameChangedEvent.getContactId().getValue(), contactDTO);
        }
    }

    public void startSubscription() {

        // TODO: make connection stuff configurable
        EventStore eventStore = EventStoreBuilder.newBuilder()
                .singleNodeAddress("127.0.0.1", 1113)
                .userCredentials("admin", "changeit")
                .build();

        CatchUpSubscriptionSettings settings = CatchUpSubscriptionSettings.newBuilder()
                .resolveLinkTos(true).build();

        // TODO: close connection/subscription on shutdown
        CatchUpSubscription catchupSubscription = eventStore.subscribeToStreamFrom("$ce-contact", null, settings, listener);

        //eventStore.subscribeToAll()
    }

    public Collection<ContactDTO> find(String firstName, String lastName) {

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        if (firstName != null) {
            andPredicates.add(Predicates.equal("firstName", firstName));
        }
        if (lastName != null) {
            andPredicates.add(Predicates.equal("lastName", lastName));
        }

        Predicate criteriaQuery = Predicates.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

        return contactMap.values(criteriaQuery);
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
        return Stream.of(ContactEventType.values())
                .filter(et -> et.getName().equals(evenType.getName()))
                .map(ContactEventType::getEventClass)
                .findFirst()
                .get();
    }

}
