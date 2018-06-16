package org.jaun.clubmanager.member.domain.model.membershiptype;

import static java.util.Objects.requireNonNull;

import java.util.Optional;

import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.eventstore.EventStream;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeEvent;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeMetadataChangedEvent;

/**
 * Type of the Membership regardless of Subscription period. E.g. 'Gönner', 'Normal', 'Passiv'
 */
public class MembershipType extends EventSourcingAggregate<MembershipTypeId, MembershipTypeEvent> {

    private MembershipTypeId id;
    private String name;
    private String description;

    public MembershipType(MembershipTypeId id, String name, String description) {
        apply(new MembershipTypeCreatedEvent(id));
        apply(new MembershipTypeMetadataChangedEvent(id, name, description));
    }

    public MembershipType(EventStream<MembershipTypeEvent> eventStream) {
        replayEvents(eventStream);
    }

    public String getName() {
        return name;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }


    public void mutate(MembershipTypeCreatedEvent event) {
        this.id = requireNonNull(event.getMembershipTypeId());
    }

    public void mutate(MembershipTypeMetadataChangedEvent event) {
        this.name = event.getName();
        this.description = event.getDescription().orElse(null);
    }

    @Override
    protected void mutate(MembershipTypeEvent event) {
        if (event instanceof MembershipTypeCreatedEvent) {
            mutate((MembershipTypeCreatedEvent) event);
        } else if (event instanceof MembershipTypeMetadataChangedEvent) {
            mutate((MembershipTypeMetadataChangedEvent) event);
        }
    }

    public MembershipTypeId getId() {
        return id;
    }
}
