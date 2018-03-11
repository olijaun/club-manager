package org.jaun.clubmanager.member.infra.projection;

import java.time.format.DateTimeFormatter;

import org.jaun.clubmanager.member.application.resource.ContactDTO;
import org.jaun.clubmanager.member.application.resource.MembershipPeriodDTO;
import org.jaun.clubmanager.member.application.resource.MembershipViewDTO;
import org.jaun.clubmanager.member.application.resource.SubscriptionDefinitionDTO;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactCreatedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactEventType;
import org.jaun.clubmanager.member.domain.model.contact.event.NameChangedEvent;
import org.jaun.clubmanager.member.domain.model.membership.MembershipId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membership.MembershipTypeRepository;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipEventType;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodEventType;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodMetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipPeriodSubscriptionDefinitionAddedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Service
public class HazelcastMembershipProjection extends AbstractProjection {

    private IMap<String, SubscriptionDefinitionDTO> subscriptionDefinitionMap;
    private IMap<String, MembershipPeriodDTO> membershipPeriodMap;
    private IMap<String, MembershipViewDTO> membershipMap;
    private IMap<String, ContactDTO> membershipContactMap;

    @Autowired
    private MembershipTypeRepository membershipTypeRepository;

    public HazelcastMembershipProjection(@Autowired HazelcastInstance hazelcastInstance) {
        super("MembershipProjectionStream");

        registerMapping(MembershipPeriodEventType.SUBSCRIPTION_DEFINITION_ADDED,
                (r) -> update(toObject(r, MembershipPeriodSubscriptionDefinitionAddedEvent.class)));

        registerMapping(MembershipPeriodEventType.MEMBERSHIP_PERIOD_CREATED,
                (r) -> update(toObject(r, MembershipPeriodCreatedEvent.class)));
        registerMapping(MembershipPeriodEventType.METADATA_CHANGED,
                (r) -> update(toObject(r, MembershipPeriodMetadataChangedEvent.class)));

        registerMapping(MembershipEventType.MEMBERSHIP_CREATED, (r) -> update(toObject(r, MembershipCreatedEvent.class)));

        registerMapping(ContactEventType.CONTACT_CREATED, (r) -> update(toObject(r, ContactCreatedEvent.class)));
        registerMapping(ContactEventType.NAME_CHANGED, (r) -> update(toObject(r, NameChangedEvent.class)));

        subscriptionDefinitionMap = hazelcastInstance.getMap("subscription-definitions");
        membershipPeriodMap = hazelcastInstance.getMap("membership-periods");
        membershipMap = hazelcastInstance.getMap("memberships");
        membershipContactMap = hazelcastInstance.getMap("membership-contacts");
    }

    protected void update(MembershipPeriodSubscriptionDefinitionAddedEvent definitionAddedEvent) {

        SubscriptionDefinitionDTO definitionDTO = new SubscriptionDefinitionDTO();

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

        ContactDTO contactDTO = membershipContactMap.get(membershipCreatedEvent.getSubscriberId().getValue());

        MembershipPeriodDTO periodDTO = membershipPeriodMap.get(membershipCreatedEvent.getMembershipPeriodId().getValue());

        SubscriptionDefinitionDTO definitionDTO =
                subscriptionDefinitionMap.get(membershipCreatedEvent.getSubscriptionDefinitionId().getValue());

        MembershipViewDTO view = new MembershipViewDTO();
        view.setMembershipId(membershipCreatedEvent.getMembershipId().getValue());
        view.setMembershipPeriodId(membershipCreatedEvent.getMembershipPeriodId().getValue());
        view.setMembershipPeriodName(periodDTO.getName());

        view.setSubscriberId(contactDTO.getContactId());
        view.setSubscriberFirstName(contactDTO.getFirstName());
        view.setSubscriberLastName(contactDTO.getLastName());

        view.setSubscriptionDefinitionId(membershipCreatedEvent.getSubscriptionDefinitionId().getValue());
        view.setSubscriptionDefinitionName(definitionDTO.getName());
        view.setMembershipTypeId(definitionDTO.getMembershipTypeId());

        // TODO: make type also event sourced
        view.setMembershipTypeName(membershipTypeRepository.get(new MembershipTypeId(definitionDTO.getMembershipTypeId())).getName());

        membershipMap.put(membershipCreatedEvent.getMembershipId().getValue(), view);

    }

    public MembershipViewDTO getById(MembershipId id) {
        return membershipMap.get(id.getValue());
    }

    protected void update(ContactCreatedEvent contactCreatedEvent) {

        ContactDTO contactDTO = new ContactDTO();
        contactDTO.setContactId(contactCreatedEvent.getContactId().getValue());

        membershipContactMap.put(contactCreatedEvent.getContactId().getValue(), contactDTO);
    }

    protected void update(NameChangedEvent nameChangedEvent) {

        ContactDTO contactDTO = membershipContactMap.get(nameChangedEvent.getContactId().getValue());
        contactDTO.setFirstName(nameChangedEvent.getFirstName());
        contactDTO.setLastName(nameChangedEvent.getLastName());

        membershipContactMap.put(nameChangedEvent.getContactId().getValue(), contactDTO);
    }

}
