package org.jaun.clubmanager.member.infra.projection;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.jaun.clubmanager.domain.model.commons.AbstractProjection;
import org.jaun.clubmanager.member.application.resource.MemberDTO;
import org.jaun.clubmanager.member.application.resource.MembershipPeriodDTO;
import org.jaun.clubmanager.member.application.resource.MembershipViewDTO;
import org.jaun.clubmanager.member.application.resource.SubscriptionOptionDTO;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;
import org.jaun.clubmanager.member.domain.model.member.event.MemberCreatedEvent;
import org.jaun.clubmanager.member.domain.model.member.event.SubscriptionCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.SubscriptionOptionId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.event.MembershipPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershipperiod.event.MembershipPeriodMetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.membershipperiod.event.MembershipPeriodSubscriptionOptionAddedEvent;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.infra.projection.event.contact.NameChangedEvent;
import org.jaun.clubmanager.member.infra.repository.MemberEventMapping;
import org.jaun.clubmanager.member.infra.repository.MembershipPeriodEventMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.StreamEventsSlice;
import com.github.msemys.esjc.StreamPosition;
import com.hazelcast.core.EntryView;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

@Service
public class HazelcastMembershipProjection extends AbstractProjection {

    private final IMap<SubscriptionOptionId, SubscriptionOptionDTO> subscriptionOptionMap;
    private final IMap<MembershipPeriodId, MembershipPeriodDTO> membershipPeriodMap;
    private final IMap<SubscriptionId, MembershipViewDTO> membershipMap;
    private final IMap<KeyWithVersion<MemberId>, MemberDTO> membershipContactMap;
    private final IMap<String, String> lockMap;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    public HazelcastMembershipProjection(@Autowired EventStore eventStore, @Autowired HazelcastInstance hazelcastInstance) {
        super(eventStore, "MembershipProjectionStream");

        registerMapping(MembershipPeriodEventMapping.SUBSCRIPTION_OPTION_ADDED,
                (v, r) -> update(v, toObject(r, MembershipPeriodSubscriptionOptionAddedEvent.class)));

        registerMapping(MembershipPeriodEventMapping.MEMBERSHIP_PERIOD_CREATED,
                (v, r) -> update(v, toObject(r, MembershipPeriodCreatedEvent.class)));
        registerMapping(MembershipPeriodEventMapping.METADATA_CHANGED,
                (v, r) -> update(v, toObject(r, MembershipPeriodMetadataChangedEvent.class)));

        registerMapping(MemberEventMapping.SUBSCRIPTION_CREATED, (v, r) -> update(v, toObject(r, SubscriptionCreatedEvent.class)));

        registerMapping(MemberEventMapping.MEMBER_CREATED, (v, r) -> update(v, toObject(r, MemberCreatedEvent.class)));

        registerMapping("NameChanged", (v, r) -> update(v, toObject(r, NameChangedEvent.class)));

        subscriptionOptionMap = hazelcastInstance.getMap("subscription-options");
        membershipPeriodMap = hazelcastInstance.getMap("membership-periods");
        membershipMap = hazelcastInstance.getMap("memberships");
        membershipContactMap = hazelcastInstance.getMap("membership-contacts");
        lockMap = hazelcastInstance.getMap("locks");
    }

    protected void update(Long version, MembershipPeriodSubscriptionOptionAddedEvent optionAddedEvent) {

        SubscriptionOptionDTO optionDTO = new SubscriptionOptionDTO();

        optionDTO.setId(optionAddedEvent.getSubscriptionOptionId().getValue());
        optionDTO.setAmount(optionAddedEvent.getAmount());
        optionDTO.setCurrency(optionAddedEvent.getCurrency().getCurrencyCode());
        optionDTO.setMaxSubscribers(optionAddedEvent.getMaxSubscribers());
        optionDTO.setMembershipPeriodId(optionAddedEvent.getMembershipPeriodId().getValue());
        optionDTO.setName(optionAddedEvent.getName());
        optionDTO.setMembershipTypeId(optionAddedEvent.getMembershipTypeId().getValue());

        subscriptionOptionMap.put(optionAddedEvent.getSubscriptionOptionId(), optionDTO);
    }

    protected void update(Long version, MembershipPeriodCreatedEvent membershipPeriodCreatedEvent) {

        MembershipPeriodDTO periodDTO = new MembershipPeriodDTO();

        periodDTO.setId(membershipPeriodCreatedEvent.getMembershipPeriodId().getValue());
        periodDTO.setStartDate(membershipPeriodCreatedEvent.getStart().format(DateTimeFormatter.ISO_DATE));
        periodDTO.setEndDate(membershipPeriodCreatedEvent.getStart().format(DateTimeFormatter.ISO_DATE));
        membershipPeriodMap.put(membershipPeriodCreatedEvent.getMembershipPeriodId(), periodDTO);
    }

    protected void update(Long version, MembershipPeriodMetadataChangedEvent metadataChangedEvent) {

        MembershipPeriodDTO periodDTO = membershipPeriodMap.get(metadataChangedEvent.getMembershipPeriodId());

        periodDTO.setName(metadataChangedEvent.getName());
        periodDTO.setDescription(metadataChangedEvent.getDescription());

        membershipPeriodMap.put(metadataChangedEvent.getMembershipPeriodId(), periodDTO);
    }

    protected void update(Long version, SubscriptionCreatedEvent subscriptionCreatedEvent) {

        MemberDTO memberDTO = membershipContactMap.get(new KeyWithVersion<>(subscriptionCreatedEvent.getMemberId(), 0));

        MembershipPeriodDTO periodDTO = membershipPeriodMap.get(subscriptionCreatedEvent.getMembershipPeriodId());

        SubscriptionOptionDTO optionDTO = subscriptionOptionMap.get(subscriptionCreatedEvent.getSubscriptionOptionId());

        MembershipViewDTO view = new MembershipViewDTO();
        view.setMembershipId(subscriptionCreatedEvent.getSubscriptionId().getValue());
        view.setMembershipPeriodId(subscriptionCreatedEvent.getMembershipPeriodId().getValue());
        view.setMembershipPeriodName(periodDTO.getName());

        view.setSubscriberId(memberDTO.getMemberId());
        view.setSubscriberFirstName(memberDTO.getFirstName());
        view.setSubscriberLastName(memberDTO.getLastName());

        view.setSubscriptionOptionId(subscriptionCreatedEvent.getSubscriptionOptionId().getValue());
        view.setSubscriptionOptionName(optionDTO.getName());
        view.setMembershipTypeId(optionDTO.getMembershipTypeId());

        // TODO: make type also event sourced
        view.setMembershipTypeName(membershipTypeRepository.get(new MembershipTypeId(optionDTO.getMembershipTypeId())).getName());

        membershipMap.put(subscriptionCreatedEvent.getSubscriptionId(), view);

    }

    public MembershipViewDTO getById(SubscriptionId id) {
        return membershipMap.get(id);
    }

    public MemberDTO getById(MemberId id) {
        return membershipContactMap.get(id);
    }

    protected void update(Long version, MemberCreatedEvent memberCreatedEvent) {

        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setMemberId(memberCreatedEvent.getMemberId().getValue());

        membershipContactMap.put(new KeyWithVersion<>(memberCreatedEvent.getMemberId(), version), memberDTO);

        try {
            StreamEventsSlice streamEventsSlice =
                    eventStore.readStreamEventsBackward("contact-" + memberCreatedEvent.getMemberId().getValue(),
                            StreamPosition.END, 4096, false).get();

            Optional<ResolvedEvent> nameChangedResolvedEvent = streamEventsSlice.events.stream()
                    .filter(resolvedEvent -> resolvedEvent.event.eventType.equals("NameChanged"))
                    .findFirst();

            if (nameChangedResolvedEvent.isPresent()) {

                NameChangedEvent nameChangedEvent = toObject(nameChangedResolvedEvent.get(), NameChangedEvent.class);

                update(nameChangedResolvedEvent.get().event.eventNumber, nameChangedEvent);
            }

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    protected void update(Long version, NameChangedEvent nameChangedEvent) {
        String lockString = "members";
        try {
            lockMap.lock(lockString);

            //MemberDTO memberDTO  =
            EntryView<KeyWithVersion<MemberId>, MemberDTO> entryView = membershipContactMap.getEntryView(
                    new KeyWithVersion<>(new MemberId(nameChangedEvent.getContactId().getValue()), 0));


            if (entryView == null) {
                return;
            }

            MemberDTO memberDTO = entryView.getValue();
            KeyWithVersion keyWithVersion = entryView.getKey();

            if (memberDTO == null || keyWithVersion.getVersion() >= version) {
                return;
            }

            memberDTO.setFirstName(nameChangedEvent.getName().getFirstName().orElse(null));
            memberDTO.setLastName(nameChangedEvent.getName().getLastNameOrCompanyName());

            // important: convert contact id to member id, because we convert contacts into members and won't find them in the map otherwise
            membershipContactMap.put(new KeyWithVersion<>(new MemberId(nameChangedEvent.getContactId().getValue()), version),
                    memberDTO);

            Collection<MembershipViewDTO> membershipViewDTOS =
                    find(null, null, null, new MemberId(nameChangedEvent.getContactId().getValue()));

            if (!membershipViewDTOS.isEmpty()) {

                MembershipViewDTO viewDTO = membershipViewDTOS.iterator().next();

                // update member info in membership view
                viewDTO.setSubscriberFirstName(nameChangedEvent.getName().getFirstName().orElse(null));
                viewDTO.setSubscriberLastName(nameChangedEvent.getName().getLastNameOrCompanyName());

                membershipMap.put(new SubscriptionId(viewDTO.getMembershipId()), viewDTO);
            }

        } finally {
            lockMap.unlock(lockString);
        }
    }

    public Collection<MembershipViewDTO> find(String firstName, String lastName, MembershipPeriodId membershipPeriodId,
            MemberId memberId) {

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        if (firstName != null) {
            andPredicates.add(Predicates.ilike("subscriberFirstName", "%" + firstName + "%"));
        }
        if (lastName != null) {
            andPredicates.add(Predicates.ilike("subscriberLastName", "%" + lastName + "%"));
        }
        if (membershipPeriodId != null) {
            andPredicates.add(Predicates.equal("membershipPeriodId", membershipPeriodId.getValue()));
        }
        if (memberId != null) {
            andPredicates.add(Predicates.equal("subscriberId", memberId.getValue()));
        }

        Predicate criteriaQuery = Predicates.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

        return membershipMap.values(criteriaQuery);
    }

    public Collection<MembershipPeriodDTO> getAllMembershipPeriods() {

        return membershipPeriodMap.values();
    }

    public Collection<SubscriptionOptionDTO> getAllSubscriptionOptionsForPeriods(MembershipPeriodId membershipPeriodId) {

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        andPredicates.add(Predicates.equal("membershipPeriodId", membershipPeriodId.getValue()));

        Predicate criteriaQuery = Predicates.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

        return subscriptionOptionMap.values(criteriaQuery);

    }

    public SubscriptionOptionDTO get(MembershipPeriodId membershipPeriodId, SubscriptionOptionId subscriptionOptionId) {

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        andPredicates.add(Predicates.equal("membershipPeriodId", membershipPeriodId.getValue()));

        andPredicates.add(Predicates.equal("id", subscriptionOptionId.getValue()));

        Predicate criteriaQuery = Predicates.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

        if (subscriptionOptionMap.values(criteriaQuery).isEmpty()) {
            return null;
        }

        return subscriptionOptionMap.values(criteriaQuery).iterator().next();

    }

    public MembershipPeriodDTO getById(MembershipPeriodId membershipPeriodId) {
        return membershipPeriodMap.get(membershipPeriodId);
    }
}
