package org.jaun.clubmanager.member.infra.projection;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.Predicates;
import org.jaun.clubmanager.eventstore.AbstractMappingCatchUpSubscriptionListener;
import org.jaun.clubmanager.eventstore.Category;
import org.jaun.clubmanager.eventstore.EventType;
import org.jaun.clubmanager.eventstore.StreamReader;
import org.jaun.clubmanager.member.application.resource.*;
import org.jaun.clubmanager.member.domain.model.member.MemberId;
import org.jaun.clubmanager.member.domain.model.member.event.MemberCreatedEvent;
import org.jaun.clubmanager.member.domain.model.member.event.SubscriptionCreatedEvent;
import org.jaun.clubmanager.member.domain.model.member.event.SubscriptionDeletedEvent;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeMetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.MetadataChangedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionTypeAddedEvent;
import org.jaun.clubmanager.member.infra.projection.event.person.BasicDataChangedEvent;
import org.jaun.clubmanager.member.infra.projection.event.person.PersonCreatedEvent;
import org.jaun.clubmanager.member.infra.projection.event.person.StreetAddress;
import org.jaun.clubmanager.member.infra.projection.event.person.StreetAddressChangedEvent;
import org.jaun.clubmanager.member.infra.repository.MemberEventMapping;
import org.jaun.clubmanager.member.infra.repository.MembershipTypeEventMapping;
import org.jaun.clubmanager.member.infra.repository.SubscriptionPeriodEventMapping;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class HazelcastMemberProjection extends AbstractMappingCatchUpSubscriptionListener {

    private final IMap<SubscriptionPeriodId, SubscriptionPeriodDTO> subscriptionPeriodMap;
    private final IMap<MembershipTypeId, MembershipTypeDTO> membershipTypeMap;
    private final IMap<MemberId, MemberDTO> memberMap;

    public HazelcastMemberProjection(HazelcastInstance hazelcastInstance) {

        registerMapping(SubscriptionPeriodEventMapping.SUBSCRIPTION_TYPE_ADDED.getEventType(),
                (v, r) -> update(v, toObject(r, SubscriptionTypeAddedEvent.class)));

        registerMapping(SubscriptionPeriodEventMapping.SUBSCRIPTION_PERIOD_CREATED.getEventType(),
                (v, r) -> update(v, toObject(r, SubscriptionPeriodCreatedEvent.class)));
        registerMapping(SubscriptionPeriodEventMapping.METADATA_CHANGED.getEventType(),
                (v, r) -> update(v, toObject(r, MetadataChangedEvent.class)));

        registerMapping(MemberEventMapping.SUBSCRIPTION_CREATED.getEventType(), (v, r) -> update(v, toObject(r, SubscriptionCreatedEvent.class)));
        registerMapping(MemberEventMapping.SUBSCRIPTION_DELETED.getEventType(), (v, r) -> update(v, toObject(r, SubscriptionDeletedEvent.class)));
        registerMapping(MemberEventMapping.MEMBER_CREATED.getEventType(), (v, r) -> update(v, toObject(r, MemberCreatedEvent.class)));


        registerMapping(MembershipTypeEventMapping.MEMBERSHIPTYPE_CREATED.getEventType(),
                (v, r) -> update(v, toObject(r, MembershipTypeCreatedEvent.class)));
        registerMapping(MembershipTypeEventMapping.MEMBERSHIPTYPE_METADATA_CHANGED.getEventType(),
                (v, r) -> update(v, toObject(r, MembershipTypeMetadataChangedEvent.class)));

        registerMapping(new EventType("PersonCreated"), (v, r) -> update(v, toObject(r, PersonCreatedEvent.class)));
        registerMapping(new EventType("BasicDataChanged"), (v, r) -> update(v, toObject(r, BasicDataChangedEvent.class)));
        registerMapping(new EventType("StreetAddressChanged"), (v, r) -> update(v, toObject(r, StreetAddressChangedEvent.class)));

        subscriptionPeriodMap = hazelcastInstance.getMap("subscription-periods");
        membershipTypeMap = hazelcastInstance.getMap("membership-type-map");
        memberMap = hazelcastInstance.getMap("members");
    }

    public Collection<Category> categories() {
        return ImmutableList.of(
                new Category("person"), //
                new Category("subscriptionperiod"), //
                new Category("member"), //
                new Category("membershiptype"));
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
        optionDTO.setAmount(optionAddedEvent.getPrice().amount().longValue());
        optionDTO.setCurrency(optionAddedEvent.getPrice().currency().getCurrencyCode());
        optionDTO.setMaxSubscribers(optionAddedEvent.getMaxSubscribers());
        optionDTO.setSubscriptionPeriodId(optionAddedEvent.getSubscriptionPeriodId().getValue());
        optionDTO.setName(optionAddedEvent.getName());
        optionDTO.setMembershipTypeId(optionAddedEvent.getMembershipTypeId().getValue());

        SubscriptionPeriodDTO periodDTO = subscriptionPeriodMap.get(optionAddedEvent.getSubscriptionPeriodId());

        periodDTO.getSubscriptionTypes().add(optionDTO);

        subscriptionPeriodMap.put(optionAddedEvent.getSubscriptionPeriodId(), periodDTO);
    }

    protected void update(Long version, SubscriptionPeriodCreatedEvent subscriptionPeriodCreatedEvent) {

        SubscriptionPeriodDTO periodDTO = new SubscriptionPeriodDTO();

        periodDTO.setId(subscriptionPeriodCreatedEvent.getSubscriptionPeriodId().getValue());
        periodDTO.setStartDate(subscriptionPeriodCreatedEvent.getStart().format(DateTimeFormatter.ISO_DATE));
        periodDTO.setEndDate(subscriptionPeriodCreatedEvent.getEnd().format(DateTimeFormatter.ISO_DATE));
        subscriptionPeriodMap.put(subscriptionPeriodCreatedEvent.getSubscriptionPeriodId(), periodDTO);
    }

    protected void update(Long version, MetadataChangedEvent metadataChangedEvent) {

        SubscriptionPeriodDTO periodDTO = subscriptionPeriodMap.get(metadataChangedEvent.getSubscriptionPeriodId());

        periodDTO.setName(metadataChangedEvent.getName());
        periodDTO.setDescription(metadataChangedEvent.getDescription());

        subscriptionPeriodMap.put(metadataChangedEvent.getSubscriptionPeriodId(), periodDTO);
    }

    protected synchronized void update(Long version, SubscriptionCreatedEvent subscriptionCreatedEvent) {
        MemberDTO memberDTO = memberMap.get(subscriptionCreatedEvent.getMemberId());

        SubscriptionDTO subscriptionDTO = new SubscriptionDTO();
        subscriptionDTO.setId(subscriptionCreatedEvent.getSubscriptionId().getValue());
        subscriptionDTO.setMemberId(subscriptionCreatedEvent.getMemberId().getValue());
        subscriptionDTO.setSubscriptionPeriodId(subscriptionCreatedEvent.getSubscriptionPeriodId().getValue());
        subscriptionDTO.setSubscriptionTypeId(subscriptionCreatedEvent.getSubscriptionTypeId().getValue());
        memberDTO.getSubscriptions().add(subscriptionDTO);

        memberMap.put(subscriptionCreatedEvent.getMemberId(), memberDTO);
    }

    protected synchronized void update(Long version, SubscriptionDeletedEvent subscriptionDeletedEvent) {
        MemberDTO memberDTO = memberMap.get(subscriptionDeletedEvent.getMemberId());

        memberDTO.getSubscriptions().stream()
                .filter(s -> s.getId().equals(subscriptionDeletedEvent.getSubscriptionId().getValue())).findFirst()
                .ifPresent(s -> memberDTO.getSubscriptions().remove(s));

        memberMap.put(subscriptionDeletedEvent.getMemberId(), memberDTO);
    }

    public MemberDTO getById(MemberId id) {
        return memberMap.get(id);
    }

    protected synchronized void update(Long version, MemberCreatedEvent memberCreatedEvent) {

        MemberDTO memberDTO = memberMap.get(memberCreatedEvent.getMemberId());

        if (memberDTO != null) {
            return;
        }
        memberDTO = new MemberDTO();
        memberDTO.setId(memberCreatedEvent.getMemberId().getValue());

        memberMap.put(memberCreatedEvent.getMemberId(), memberDTO);

    }

    protected synchronized void update(Long version, PersonCreatedEvent personCreatedEvent) {

        MemberId memberId = new MemberId(personCreatedEvent.getPersonId().getValue());
        MemberDTO memberDTO = memberMap.get(memberId);

        if (memberDTO != null) {
            return;
        }
        memberDTO = new MemberDTO();
        memberDTO.setId(personCreatedEvent.getPersonId().getValue());

        memberMap.put(memberId, memberDTO);

    }

    protected synchronized void update(Long version, BasicDataChangedEvent basicDataChangedEvent) {

        MemberDTO memberDTO = memberMap.get(new MemberId(basicDataChangedEvent.getPersonId().getValue()));

        memberDTO.setFirstName(basicDataChangedEvent.getName().getFirstName().orElse(null));
        memberDTO.setLastNameOrCompanyName(basicDataChangedEvent.getName().getLastNameOrCompanyName());

        // important: convert person id to member id, because we convert contacts into members and won't find them in the map otherwise
        memberMap.put(new MemberId(basicDataChangedEvent.getPersonId().getValue()), memberDTO);

    }

    protected synchronized void update(Long version, StreetAddressChangedEvent streetAddressChangedEvent) {
        MemberId memberId = new MemberId(streetAddressChangedEvent.getPersonId().getValue());
        MemberDTO memberDTO = memberMap.get(memberId);

        if (memberDTO == null) {
            return;
        }

        StreetAddress address = streetAddressChangedEvent.getStreetAddress();

        String streetAndHouseNumber = Joiner.on(" ").skipNulls().join(address.getStreet(), address.getHouseNumber().orElse(null));
        String zipAndCity = Joiner.on(" ").skipNulls().join(address.getZip(), address.getCity());
        String streetAddressLine = Joiner.on(", ").skipNulls().join(streetAndHouseNumber, zipAndCity);
        memberDTO.setAddress(streetAddressLine);

        memberMap.put(memberId, memberDTO);
    }

    public Optional<MemberDTO> getMember(MemberId memberId) {
        return Optional.ofNullable(memberMap.get(memberId)).map(memberDTO -> {
            Collection<SubscriptionDTO> subscriptions = getSubscriptions(memberId);
            memberDTO.setSubscriptions(subscriptions);
            return memberDTO;
        });
    }


    public Collection<SubscriptionPeriodDTO> getAllSubscriptionPeriods() {

        return subscriptionPeriodMap.values()
                .stream()
                .peek(p -> p.setSubscriptionTypes(getAllSubscriptionPeriodTypes(new SubscriptionPeriodId(p.getId()))))
                .collect(Collectors.toList());
    }

    public Collection<SubscriptionTypeDTO> getAllSubscriptionPeriodTypes(SubscriptionPeriodId subscriptionPeriodId) {

        SubscriptionPeriodDTO periodDTO = subscriptionPeriodMap.get(subscriptionPeriodId);

        return periodDTO.getSubscriptionTypes();
    }

    public Optional<SubscriptionTypeDTO> getSubscriptionType(SubscriptionPeriodId subscriptionPeriodId,
                                                             SubscriptionTypeId subscriptionTypeId) {

        return getAllSubscriptionPeriodTypes(subscriptionPeriodId).stream()
                .filter(t -> t.getId().equals(subscriptionTypeId.getValue()))
                .findFirst();

    }

    public Collection<SubscriptionDTO> getSubscriptions(MemberId memberId) {

        MemberDTO memberDTO = memberMap.get(memberId);

        for (SubscriptionDTO subscriptionDTO : memberDTO.getSubscriptions()) {

            Optional<SubscriptionTypeDTO> subscriptionType =
                    getSubscriptionType(new SubscriptionPeriodId(subscriptionDTO.getSubscriptionPeriodId()),
                            new SubscriptionTypeId(subscriptionDTO.getSubscriptionTypeId()));

            SubscriptionPeriodDTO subscriptionPeriodDTO =
                    subscriptionPeriodMap.get(new SubscriptionPeriodId(subscriptionDTO.getSubscriptionPeriodId()));
            if (subscriptionType.isPresent() && subscriptionPeriodDTO != null) {
                String info = subscriptionPeriodDTO.getName() + " / " + subscriptionType.get().getName();
                subscriptionDTO.setSubscriptionDisplayInfo(info);
            }
        }

        return memberDTO.getSubscriptions();
    }

    public SubscriptionPeriodDTO getById(SubscriptionPeriodId subscriptionPeriodId) {

        Collection<SubscriptionTypeDTO> subscriptionTypeDTOS = getAllSubscriptionPeriodTypes(subscriptionPeriodId);

        SubscriptionPeriodDTO subscriptionPeriodDTO = subscriptionPeriodMap.get(subscriptionPeriodId);
        subscriptionPeriodDTO.setSubscriptionTypes(subscriptionTypeDTOS);

        return subscriptionPeriodDTO;
    }

    public Collection<MembershipTypeDTO> getAllMembershipTypes() {
        return membershipTypeMap.values();
    }

    public Collection<MemberDTO> getMembers(String sortBy, boolean ascending) {
        return searchMembers("", null, sortBy, ascending);
    }

    public Collection<MemberDTO> searchMembers(String searchString, String subscriptionPeriodId, String sortBy, boolean ascending) {

        if (searchString == null) {
            searchString = "";
        }

        Predicate idPredicate = Predicates.ilike("id", "%" + searchString + "%");
        Predicate firstNamePredicate = Predicates.ilike("firstName", "%" + searchString + "%");
        Predicate lastNamePredicate = Predicates.ilike("lastNameOrCompanyName", "%" + searchString + "%");

        Predicate searchStringPredicate = Predicates.or(idPredicate, firstNamePredicate, lastNamePredicate);

        Predicate criteriaQuery;
        if (subscriptionPeriodId == null) {
            criteriaQuery = searchStringPredicate;
        } else {
            Predicate subscriptionPeriodIdPredicate =
                    Predicates.equal("subscriptions[any].subscriptionPeriodId", subscriptionPeriodId);

            System.out.println("search by period = " + subscriptionPeriodId);

            criteriaQuery = Predicates.and(searchStringPredicate, subscriptionPeriodIdPredicate);
        }

        Comparator<Map.Entry> descendingComparator = (e1, e2) -> {
            MemberDTO s1 = (MemberDTO) e1.getValue();
            MemberDTO s2 = (MemberDTO) e2.getValue();

            int factor = ascending ? 1 : -1;

            if (sortBy == null || sortBy.equals("lastNameOrCompanyName")) {
                return factor * s1.getLastNameOrCompanyName().compareTo(s2.getLastNameOrCompanyName());
            } else if (sortBy.equals("firstName")) {
                return factor * s1.getFirstName().compareTo(s2.getFirstName());
            } else if (sortBy.equals("id")) {
                return factor * s1.getId().compareTo(s2.getId());
            } else {
                return 0;
            }
        };

        PagingPredicate pagingPredicate = new PagingPredicate(criteriaQuery, descendingComparator, 1000);

        return memberMap.values(pagingPredicate).stream().map(memberDTO -> {
            Collection<SubscriptionDTO> subscriptions = getSubscriptions(new MemberId(memberDTO.getId()));
            memberDTO.setSubscriptions(subscriptions);
            return memberDTO;
        }).collect(Collectors.toList());
    }
}
