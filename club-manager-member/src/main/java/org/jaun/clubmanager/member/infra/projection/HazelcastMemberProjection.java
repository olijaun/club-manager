package org.jaun.clubmanager.member.infra.projection;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.jaun.clubmanager.domain.model.commons.AbstractPollingProjection;
import org.jaun.clubmanager.eventstore.EventStoreClient;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.member.application.resource.MemberDTO;
import org.jaun.clubmanager.member.application.resource.MembershipTypeDTO;
import org.jaun.clubmanager.member.application.resource.SubscriptionPeriodDTO;
import org.jaun.clubmanager.member.application.resource.SubscriptionTypeDTO;
import org.jaun.clubmanager.member.application.resource.SubscriptionViewDTO;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;
import org.jaun.clubmanager.member.domain.model.member.event.MemberCreatedEvent;
import org.jaun.clubmanager.member.domain.model.member.event.SubscriptionCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeMetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.MetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionTypeAddedEvent;
import org.jaun.clubmanager.member.infra.projection.event.contact.NameChangedEvent;
import org.jaun.clubmanager.member.infra.repository.MemberEventMapping;
import org.jaun.clubmanager.member.infra.repository.MembershipTypeEventMapping;
import org.jaun.clubmanager.member.infra.repository.SubscriptionPeriodEventMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

@Service
public class HazelcastMemberProjection extends AbstractPollingProjection {

    private final IMap<SubscriptionTypeId, SubscriptionTypeDTO> subscriptionTypeMap;
    private final IMap<SubscriptionPeriodId, SubscriptionPeriodDTO> subscriptionPeriodMap;
    private final IMap<SubscriptionId, SubscriptionViewDTO> subscriptionMap;
    private final IMap<MembershipTypeId, MembershipTypeDTO> membershipTypeMap;
    private final IMap<MemberId, MemberDTO> memberMap;

    public HazelcastMemberProjection(@Autowired EventStoreClient eventStore, @Autowired HazelcastInstance hazelcastInstance) {
        super(eventStore, "$ce-contact", "$ce-subscriptionperiod", "$ce-member", "$ce-membershiptype");

        registerMapping(SubscriptionPeriodEventMapping.SUBSCRIPTION_TYPE_ADDED,
                (v, r) -> update(v, toObject(r, SubscriptionTypeAddedEvent.class)));

        registerMapping(SubscriptionPeriodEventMapping.SUBSCRIPTION_PERIOD_CREATED,
                (v, r) -> update(v, toObject(r, SubscriptionPeriodCreatedEvent.class)));
        registerMapping(SubscriptionPeriodEventMapping.METADATA_CHANGED,
                (v, r) -> update(v, toObject(r, MetadataChangedEvent.class)));

        registerMapping(MemberEventMapping.SUBSCRIPTION_CREATED, (v, r) -> update(v, toObject(r, SubscriptionCreatedEvent.class)));
        registerMapping(MemberEventMapping.MEMBER_CREATED, (v, r) -> update(v, toObject(r, MemberCreatedEvent.class)));


        registerMapping(MembershipTypeEventMapping.MEMBERSHIPTYPE_CREATED,
                (v, r) -> update(v, toObject(r, MembershipTypeCreatedEvent.class)));
        registerMapping(MembershipTypeEventMapping.MEMBERSHIPTYPE_METADATA_CHANGED,
                (v, r) -> update(v, toObject(r, MembershipTypeMetadataChangedEvent.class)));

        registerMapping(new EventType("NameChanged"), (v, r) -> update(v, toObject(r, NameChangedEvent.class)));

        subscriptionTypeMap = hazelcastInstance.getMap("subscription-types");
        subscriptionPeriodMap = hazelcastInstance.getMap("subscription-periods");
        subscriptionMap = hazelcastInstance.getMap("subscriptions");
        membershipTypeMap = hazelcastInstance.getMap("membership-type-map");
        memberMap = hazelcastInstance.getMap("members");
    }

    private void update(Long version, MembershipTypeCreatedEvent t) {
        MembershipTypeDTO membershipTypeDTO = new MembershipTypeDTO();
        membershipTypeDTO.setId(t.getMembershipTypeId().getValue());

        membershipTypeMap.put(t.getMembershipTypeId(), membershipTypeDTO);
    }

    private void update(Long version, MembershipTypeMetadataChangedEvent t) {
        MembershipTypeDTO membershipTypeDTO = membershipTypeMap.get(t.getMembershipTypeId());

        membershipTypeDTO.setName(t.getName());
        membershipTypeDTO.setDescription(t.getDescription().orElse(null));

        membershipTypeMap.put(t.getMembershipTypeId(), membershipTypeDTO);
    }

    protected void update(Long version, SubscriptionTypeAddedEvent optionAddedEvent) {

        SubscriptionTypeDTO optionDTO = new SubscriptionTypeDTO();

        optionDTO.setId(optionAddedEvent.getSubscriptionTypeId().getValue());
        optionDTO.setAmount(optionAddedEvent.getAmount());
        optionDTO.setCurrency(optionAddedEvent.getCurrency().getCurrencyCode());
        optionDTO.setMaxSubscribers(optionAddedEvent.getMaxSubscribers());
        optionDTO.setSubscriptionPeriodId(optionAddedEvent.getSubscriptionPeriodId().getValue());
        optionDTO.setName(optionAddedEvent.getName());
        optionDTO.setMembershipTypeId(optionAddedEvent.getMembershipTypeId().getValue());

        subscriptionTypeMap.put(optionAddedEvent.getSubscriptionTypeId(), optionDTO);
    }

    protected void update(Long version, SubscriptionPeriodCreatedEvent subscriptionPeriodCreatedEvent) {

        SubscriptionPeriodDTO periodDTO = new SubscriptionPeriodDTO();

        periodDTO.setId(subscriptionPeriodCreatedEvent.getSubscriptionPeriodId().getValue());
        periodDTO.setStartDate(subscriptionPeriodCreatedEvent.getStart().format(DateTimeFormatter.ISO_DATE));
        periodDTO.setEndDate(subscriptionPeriodCreatedEvent.getStart().format(DateTimeFormatter.ISO_DATE));
        subscriptionPeriodMap.put(subscriptionPeriodCreatedEvent.getSubscriptionPeriodId(), periodDTO);
    }

    protected void update(Long version, MetadataChangedEvent metadataChangedEvent) {

        SubscriptionPeriodDTO periodDTO = subscriptionPeriodMap.get(metadataChangedEvent.getSubscriptionPeriodId());

        periodDTO.setName(metadataChangedEvent.getName());
        periodDTO.setDescription(metadataChangedEvent.getDescription());

        subscriptionPeriodMap.put(metadataChangedEvent.getSubscriptionPeriodId(), periodDTO);
    }

    protected void update(Long version, SubscriptionCreatedEvent subscriptionCreatedEvent) {

        SubscriptionViewDTO view = new SubscriptionViewDTO();
        view.setSubscriptionId(subscriptionCreatedEvent.getSubscriptionId().getValue());
        view.setSubscriptionPeriodId(subscriptionCreatedEvent.getSubscriptionPeriodId().getValue());
        view.setMemberId(subscriptionCreatedEvent.getMemberId().getValue());
        view.setSubscriptionTypeId(subscriptionCreatedEvent.getSubscriptionTypeId().getValue());
        subscriptionMap.put(subscriptionCreatedEvent.getSubscriptionId(), view);

    }

    public SubscriptionViewDTO getById(SubscriptionId id) {
        return enrich(subscriptionMap.get(id));
    }

    public MemberDTO getById(MemberId id) {
        return memberMap.get(id);
    }

    protected synchronized void update(Long version, MemberCreatedEvent memberCreatedEvent) {

        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setMemberId(memberCreatedEvent.getMemberId().getValue());

        memberMap.put(memberCreatedEvent.getMemberId(), memberDTO);

        stopSubscription("$ce-contact");

        // TODO: activate again
//        try {
//            StreamEventsSlice streamEventsSlice =
//                    eventStore.readStreamEventsBackward("contact-" + memberCreatedEvent.getMemberId().getValue(),
//                            StreamPosition.END, 4096, false).get();
//
//            Optional<ResolvedEvent> nameChangedResolvedEvent = streamEventsSlice.events.stream()
//                    .filter(resolvedEvent -> resolvedEvent.event.eventType.equals("NameChanged"))
//                    .findFirst();
//
//            if (nameChangedResolvedEvent.isPresent()) {
//                NameChangedEvent nameChangedEvent = toObject(nameChangedResolvedEvent.get(), NameChangedEvent.class);
//                update(nameChangedResolvedEvent.get().event.eventNumber, nameChangedEvent);
//            }
//
//            startSubscription("$ce-contact", version);
//
//        } catch (InterruptedException | ExecutionException e) {
//            throw new RuntimeException(e);
//        }
    }

    protected synchronized void update(Long version, NameChangedEvent nameChangedEvent) {
        String lockString = "members";
        //try {
        //lockMap.lock(lockString);

        MemberDTO memberDTO = memberMap.get(new MemberId(nameChangedEvent.getContactId().getValue()));

        if (memberDTO == null) {
            return;
        }

        memberDTO.setFirstName(nameChangedEvent.getName().getFirstName().orElse(null));
        memberDTO.setLastNameOrCompanyName(nameChangedEvent.getName().getLastNameOrCompanyName());

        // important: convert contact id to member id, because we convert contacts into members and won't find them in the map otherwise
        memberMap.put(new MemberId(nameChangedEvent.getContactId().getValue()), memberDTO);

//        } finally {
//            lockMap.unlock(lockString);
//        }
    }

    public Collection<SubscriptionViewDTO> find(String firstName, String lastName, SubscriptionPeriodId subscriptionPeriodId,
            MemberId memberId) {

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        if (firstName != null) {
            andPredicates.add(Predicates.ilike("memberFirstName", "%" + firstName + "%"));
        }
        if (lastName != null) {
            andPredicates.add(Predicates.ilike("memberLastName", "%" + lastName + "%"));
        }
        if (subscriptionPeriodId != null) {
            andPredicates.add(Predicates.equal("subscriptionPeriodId", subscriptionPeriodId.getValue()));
        }
        if (memberId != null) {
            andPredicates.add(Predicates.equal("memberId", memberId.getValue()));
        }

        Predicate criteriaQuery = Predicates.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

        return subscriptionMap.values(criteriaQuery).stream().map(this::enrich).collect(Collectors.toList());
    }

    private SubscriptionViewDTO enrich(SubscriptionViewDTO view) {

        MemberDTO memberDTO = memberMap.get(new MemberId(view.getMemberId()));
        SubscriptionPeriodDTO periodDTO = subscriptionPeriodMap.get(new SubscriptionPeriodId(view.getSubscriptionPeriodId()));
        SubscriptionTypeDTO subscriptionTypeDTO = subscriptionTypeMap.get(new SubscriptionTypeId(view.getSubscriptionTypeId()));

        view.setMemberLastName(memberDTO.getLastNameOrCompanyName());
        view.setMemberFirstName(memberDTO.getFirstName());
        view.setSubscriptionPeriodName(periodDTO.getName());
        view.setSubscriptionTypeName(subscriptionTypeDTO.getName());

        MembershipTypeDTO membershipTypeDTO =
                membershipTypeMap.get(new MembershipTypeId(subscriptionTypeDTO.getMembershipTypeId()));

        view.setMembershipTypeId(subscriptionTypeDTO.getMembershipTypeId());
        view.setMembershipTypeName(membershipTypeDTO.getName());

        return view;
    }

    public Collection<SubscriptionPeriodDTO> getAllSubscriptionPeriods() {

        return subscriptionPeriodMap.values();
    }

    public Collection<SubscriptionTypeDTO> getAllSubscriptionPeriodTypes(SubscriptionPeriodId subscriptionPeriodId) {

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        andPredicates.add(Predicates.equal("subscriptionPeriodId", subscriptionPeriodId.getValue()));

        Predicate criteriaQuery = Predicates.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

        return subscriptionTypeMap.values(criteriaQuery);

    }

    public SubscriptionTypeDTO get(SubscriptionPeriodId subscriptionPeriodId, SubscriptionTypeId subscriptionTypeId) {

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        andPredicates.add(Predicates.equal("subscriptionPeriodId", subscriptionPeriodId.getValue()));

        andPredicates.add(Predicates.equal("id", subscriptionTypeId.getValue()));

        Predicate criteriaQuery = Predicates.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

        if (subscriptionTypeMap.values(criteriaQuery).isEmpty()) {
            return null;
        }

        return subscriptionTypeMap.values(criteriaQuery).iterator().next();

    }

    public SubscriptionPeriodDTO getById(SubscriptionPeriodId subscriptionPeriodId) {
        return subscriptionPeriodMap.get(subscriptionPeriodId);
    }

    public Collection<MembershipTypeDTO> getAllMembershipTypes() {
        return membershipTypeMap.values();
    }
}
