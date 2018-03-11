package org.jaun.clubmanager.member.infra.projection;

import org.jaun.clubmanager.member.application.resource.ContactDTO;
import org.jaun.clubmanager.member.application.resource.MembershipViewDTO;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactCreatedEvent;
import org.jaun.clubmanager.member.domain.model.contact.event.ContactEventType;
import org.jaun.clubmanager.member.domain.model.contact.event.NameChangedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membership.event.MembershipEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

@Service
public class HazelcastMembershipProjection extends AbstractProjection {

    private IMap<String, ContactDTO> membershipMap;
    private IMap<String, ContactDTO> membershipContactMap;

    public HazelcastMembershipProjection(@Autowired HazelcastInstance hazelcastInstance) {
        super("MembershipProjectionStream");

        registerMapping(MembershipEventType.MEMBERSHIP_CREATED, (r) -> update(toObject(r, MembershipCreatedEvent.class)));

        registerMapping(ContactEventType.CONTACT_CREATED, (r) -> update(toObject(r, ContactCreatedEvent.class)));
        registerMapping(ContactEventType.NAME_CHANGED, (r) -> update(toObject(r, NameChangedEvent.class)));

        membershipMap = hazelcastInstance.getMap("memberships");
        membershipContactMap = hazelcastInstance.getMap("membership-contacts");
    }

    protected void update(MembershipCreatedEvent membershipCreatedEvent) {

        ContactDTO contactDTO = membershipContactMap.get(membershipCreatedEvent.getSubscriberId().getValue());

        MembershipViewDTO view = new MembershipViewDTO();
        view.setMembershipId(membershipCreatedEvent.getMembershipId().getValue());
        view.setMembershipPeriodId(membershipCreatedEvent.getMembershipPeriodId().getValue());
//        view.setMembershipPeriodName();
//        view.setMembershipTypeId();
//        view.setMembershipTypeName();
        view.setSubscriberFirstName(contactDTO.getFirstName());
//        view.setSubscriberLastName();
//        view.setSubscriptionDefinitionId();
//        view.setSubscriptionDefinitionName();

        System.out.println("membership created");
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
