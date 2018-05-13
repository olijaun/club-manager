package org.jaun.clubmanager.member.infra.projection;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import org.jaun.clubmanager.domain.model.commons.AbstractProjection;
import org.jaun.clubmanager.member.application.resource.MemberDTO;
import org.jaun.clubmanager.member.application.resource.MembershipPeriodDTO;
import org.jaun.clubmanager.member.application.resource.MembershipViewDTO;
import org.jaun.clubmanager.member.application.resource.SubscriptionOptionDTO;
import org.jaun.clubmanager.member.domain.model.membership.MemberId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipId;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershipperiod.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membershipperiod.event.MembershipPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershipperiod.event.MembershipPeriodMetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.membershipperiod.event.MembershipPeriodSubscriptionOptionAddedEvent;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeRepository;
import org.jaun.clubmanager.member.infra.projection.event.contact.ContactCreatedEvent;
import org.jaun.clubmanager.member.infra.projection.event.contact.NameChangedEvent;
import org.jaun.clubmanager.member.infra.repository.MembershipEventMapping;
import org.jaun.clubmanager.member.infra.repository.MembershipPeriodEventMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.msemys.esjc.EventStore;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

@Service
public class HazelcastMembershipProjection extends AbstractProjection {

    private final IMap<String, SubscriptionOptionDTO> subscriptionOptionMap;
    private final IMap<String, MembershipPeriodDTO> membershipPeriodMap;
    private final IMap<String, MembershipViewDTO> membershipMap;
    private final IMap<String, MemberDTO> membershipContactMap;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    public HazelcastMembershipProjection(@Autowired EventStore eventStore, @Autowired HazelcastInstance hazelcastInstance) {
        super(eventStore, "MembershipProjectionStream");

        registerMapping(MembershipPeriodEventMapping.SUBSCRIPTION_OPTION_ADDED,
                (r) -> update(toObject(r, MembershipPeriodSubscriptionOptionAddedEvent.class)));

        registerMapping(MembershipPeriodEventMapping.MEMBERSHIP_PERIOD_CREATED,
                (r) -> update(toObject(r, MembershipPeriodCreatedEvent.class)));
        registerMapping(MembershipPeriodEventMapping.METADATA_CHANGED,
                (r) -> update(toObject(r, MembershipPeriodMetadataChangedEvent.class)));

        registerMapping(MembershipEventMapping.MEMBERSHIP_CREATED, (r) -> update(toObject(r, MembershipCreatedEvent.class)));

        registerMapping("ContactCreated", (r) -> update(toObject(r, ContactCreatedEvent.class)));
        registerMapping("NameChanged", (r) -> update(toObject(r, NameChangedEvent.class)));

        subscriptionOptionMap = hazelcastInstance.getMap("subscription-options");
        membershipPeriodMap = hazelcastInstance.getMap("membership-periods");
        membershipMap = hazelcastInstance.getMap("memberships");
        membershipContactMap = hazelcastInstance.getMap("membership-contacts");
    }

    protected void update(MembershipPeriodSubscriptionOptionAddedEvent optionAddedEvent) {

        SubscriptionOptionDTO optionDTO = new SubscriptionOptionDTO();

        optionDTO.setId(optionAddedEvent.getSubscriptionOptionId().getValue());
        optionDTO.setAmount(optionAddedEvent.getAmount());
        optionDTO.setCurrency(optionAddedEvent.getCurrency().getCurrencyCode());
        optionDTO.setMaxSubscribers(optionAddedEvent.getMaxSubscribers());
        optionDTO.setMembershipPeriodId(optionAddedEvent.getMembershipPeriodId().getValue());
        optionDTO.setName(optionAddedEvent.getName());
        optionDTO.setMembershipTypeId(optionAddedEvent.getMembershipTypeId().getValue());

        subscriptionOptionMap.put(optionAddedEvent.getSubscriptionOptionId().getValue(), optionDTO);
    }

    protected void update(MembershipPeriodCreatedEvent membershipPeriodCreatedEvent) {

        MembershipPeriodDTO periodDTO = new MembershipPeriodDTO();

        periodDTO.setStartDate(membershipPeriodCreatedEvent.getStart().format(DateTimeFormatter.ISO_DATE));
        periodDTO.setEndDate(membershipPeriodCreatedEvent.getStart().format(DateTimeFormatter.ISO_DATE));

        membershipPeriodMap.put(membershipPeriodCreatedEvent.getMembershipPeriodId().getValue(), periodDTO);
    }

    protected void update(MembershipPeriodMetadataChangedEvent metadataChangedEvent) {

        MembershipPeriodDTO periodDTO = membershipPeriodMap.get(metadataChangedEvent.getMembershipPeriodId().getValue());

        periodDTO.setName(metadataChangedEvent.getName());
        periodDTO.setDescription(metadataChangedEvent.getDescription());

        membershipPeriodMap.put(metadataChangedEvent.getMembershipPeriodId().getValue(), periodDTO);
    }

    protected void update(MembershipCreatedEvent membershipCreatedEvent) {

        MemberDTO memberDTO = membershipContactMap.get(membershipCreatedEvent.getMemberId().getValue());

        MembershipPeriodDTO periodDTO = membershipPeriodMap.get(membershipCreatedEvent.getMembershipPeriodId().getValue());

        SubscriptionOptionDTO optionDTO = subscriptionOptionMap.get(membershipCreatedEvent.getSubscriptionOptionId().getValue());

        MembershipViewDTO view = new MembershipViewDTO();
        view.setMembershipId(membershipCreatedEvent.getMembershipId().getValue());
        view.setMembershipPeriodId(membershipCreatedEvent.getMembershipPeriodId().getValue());
        view.setMembershipPeriodName(periodDTO.getName());

        view.setSubscriberId(memberDTO.getMemberId());
        view.setSubscriberFirstName(memberDTO.getFirstName());
        view.setSubscriberLastName(memberDTO.getLastName());

        view.setSubscriptionOptionId(membershipCreatedEvent.getSubscriptionOptionId().getValue());
        view.setSubscriptionOptionName(optionDTO.getName());
        view.setMembershipTypeId(optionDTO.getMembershipTypeId());

        // TODO: make type also event sourced
        view.setMembershipTypeName(membershipTypeRepository.get(new MembershipTypeId(optionDTO.getMembershipTypeId())).getName());

        membershipMap.put(membershipCreatedEvent.getMembershipId().getValue(), view);

    }

    public MembershipViewDTO getById(MembershipId id) {
        return membershipMap.get(id.getValue());
    }

    public MemberDTO getById(MemberId id) {
        return membershipContactMap.get(id.getValue());
    }

    protected void update(ContactCreatedEvent contactCreatedEvent) {

        MemberDTO contactDTO = new MemberDTO();
        contactDTO.setMemberId(contactCreatedEvent.getContactId().getValue());

        membershipContactMap.put(contactCreatedEvent.getContactId().getValue(), contactDTO);
    }

    protected void update(NameChangedEvent nameChangedEvent) {

        MemberDTO memberDTO = membershipContactMap.get(nameChangedEvent.getContactId().getValue());
        memberDTO.setFirstName(nameChangedEvent.getFirstName());
        memberDTO.setLastName(nameChangedEvent.getLastName());

        membershipContactMap.put(nameChangedEvent.getContactId().getValue(), memberDTO);
    }

    public Collection<MembershipViewDTO> find(String firstName, String lastName, MembershipPeriodId membershipPeriodId) {

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

    public MembershipPeriodDTO getById(MembershipPeriodId membershipPeriodId) {
        return membershipPeriodMap.get(membershipPeriodId.getValue());
    }
}
