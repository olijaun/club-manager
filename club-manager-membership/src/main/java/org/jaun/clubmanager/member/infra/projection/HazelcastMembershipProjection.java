package org.jaun.clubmanager.member.infra.projection;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;

import org.jaun.clubmanager.domain.model.commons.AbstractProjection;
import org.jaun.clubmanager.member.application.resource.MemberDTO;
import org.jaun.clubmanager.member.application.resource.MembershipPeriodDTO;
import org.jaun.clubmanager.member.application.resource.MembershipViewDTO;
import org.jaun.clubmanager.member.application.resource.SubscriptionDefinitionDTO;
import org.jaun.clubmanager.member.domain.model.membership.MemberId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipPeriodId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeRepository;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipEventType;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodEventType;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodMetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodSubscriptionDefinitionAddedEvent;
import org.jaun.clubmanager.member.infra.projection.event.contact.ContactCreatedEvent;
import org.jaun.clubmanager.member.infra.projection.event.contact.NameChangedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.msemys.esjc.EventStore;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;

@Service
public class HazelcastMembershipProjection extends AbstractProjection {

    private final IMap<String, SubscriptionDefinitionDTO> subscriptionDefinitionMap;
    private final IMap<String, MembershipPeriodDTO> membershipPeriodMap;
    private final IMap<String, MembershipViewDTO> membershipMap;
    private final IMap<String, MemberDTO> membershipContactMap;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    public HazelcastMembershipProjection(@Autowired EventStore eventStore, @Autowired HazelcastInstance hazelcastInstance) {
        super(eventStore, "MembershipProjectionStream");

        registerMapping(MembershipPeriodEventType.SUBSCRIPTION_DEFINITION_ADDED,
                (r) -> update(toObject(r, MembershipPeriodSubscriptionDefinitionAddedEvent.class)));

        registerMapping(MembershipPeriodEventType.MEMBERSHIP_PERIOD_CREATED,
                (r) -> update(toObject(r, MembershipPeriodCreatedEvent.class)));
        registerMapping(MembershipPeriodEventType.METADATA_CHANGED,
                (r) -> update(toObject(r, MembershipPeriodMetadataChangedEvent.class)));

        registerMapping(MembershipEventType.MEMBERSHIP_CREATED, (r) -> update(toObject(r, MembershipCreatedEvent.class)));

        registerMapping("ContactCreated", (r) -> update(toObject(r, ContactCreatedEvent.class)));
        registerMapping("NameChanged", (r) -> update(toObject(r, NameChangedEvent.class)));

        subscriptionDefinitionMap = hazelcastInstance.getMap("subscription-definitions");
        membershipPeriodMap = hazelcastInstance.getMap("membership-periods");
        membershipMap = hazelcastInstance.getMap("memberships");
        membershipContactMap = hazelcastInstance.getMap("membership-contacts");
    }

    protected void update(MembershipPeriodSubscriptionDefinitionAddedEvent definitionAddedEvent) {

        SubscriptionDefinitionDTO definitionDTO = new SubscriptionDefinitionDTO();

        definitionDTO.setId(definitionAddedEvent.getSubscriptionDefinitionId().getValue());
        definitionDTO.setAmount(definitionAddedEvent.getAmount());
        definitionDTO.setCurrency(definitionAddedEvent.getCurrency().getCurrencyCode());
        definitionDTO.setMaxSubscribers(definitionAddedEvent.getMaxSubscribers());
        definitionDTO.setMembershipPeriodId(definitionAddedEvent.getMembershipPeriodId().getValue());
        definitionDTO.setName(definitionAddedEvent.getName());
        definitionDTO.setMembershipTypeId(definitionAddedEvent.getMembershipTypeId().getValue());

        subscriptionDefinitionMap.put(definitionAddedEvent.getSubscriptionDefinitionId().getValue(), definitionDTO);
    }

    protected void update(MembershipPeriodCreatedEvent membershipPeriodCreatedEvent) {

        MembershipPeriodDTO periodDTO = new MembershipPeriodDTO();

        periodDTO.setId(membershipPeriodCreatedEvent.getMembershipPeriodId().getValue());
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

        SubscriptionDefinitionDTO definitionDTO =
                subscriptionDefinitionMap.get(membershipCreatedEvent.getSubscriptionDefinitionId().getValue());

        MembershipViewDTO view = new MembershipViewDTO();
        view.setMembershipId(membershipCreatedEvent.getMembershipId().getValue());
        view.setMembershipPeriodId(membershipCreatedEvent.getMembershipPeriodId().getValue());
        view.setMembershipPeriodName(periodDTO.getName());

        view.setSubscriberId(memberDTO.getMemberId());
        view.setSubscriberFirstName(memberDTO.getFirstName());
        view.setSubscriberLastName(memberDTO.getLastName());

        view.setSubscriptionDefinitionId(membershipCreatedEvent.getSubscriptionDefinitionId().getValue());
        view.setSubscriptionDefinitionName(definitionDTO.getName());
        view.setMembershipTypeId(definitionDTO.getMembershipTypeId());

        // TODO: make type also event sourced
        view.setMembershipTypeName(
                membershipTypeRepository.get(new MembershipTypeId(definitionDTO.getMembershipTypeId())).getName());

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

    public Collection<SubscriptionDefinitionDTO> getAllSubscriptionDefinitionsForPeriods(MembershipPeriodId membershipPeriodId) {

        ArrayList<Predicate> andPredicates = new ArrayList<>();

        andPredicates.add(Predicates.equal("membershipPeriodId", membershipPeriodId.getValue()));

        Predicate criteriaQuery = Predicates.and(andPredicates.toArray(new Predicate[andPredicates.size()]));

        return subscriptionDefinitionMap.values(criteriaQuery);

    }
}
