package org.jaun.clubmanager.member.domain.model.member;

import org.jaun.clubmanager.domain.model.commons.DomainEvent;
import org.jaun.clubmanager.domain.model.commons.Entity;
import org.jaun.clubmanager.domain.model.commons.EventSourcingAggregate;
import org.jaun.clubmanager.domain.model.commons.EventStream;
import org.jaun.clubmanager.member.domain.model.member.event.MemberCreatedEvent;

import static java.util.Objects.requireNonNull;

public class Member extends EventSourcingAggregate<MemberId> {

    private MemberId id;
    private String firstName;
    private String lastName;
    private Address address;
    private PhoneNumber phone;
    private EmailAddress emailAddress;

    public Member(MemberId id, String firstName, String lastName) {
        apply(new MemberCreatedEvent(id, firstName, lastName));
    }

    public Member(EventStream<Member> eventStream) {
        replayEvents(eventStream);
    }

    protected void mutate(MemberCreatedEvent event) {
        this.id = requireNonNull(event.getMemberId());
        this.firstName = requireNonNull(event.getFirstName());
        this.lastName = requireNonNull(event.getLastName());
    }

    @Override
    protected void mutate(DomainEvent event) {
        if(event instanceof MemberCreatedEvent) {
            mutate((MemberCreatedEvent)event);
        }
    }

    public MemberId getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Address getAddress() {
        return address;
    }

    public PhoneNumber getPhone() {
        return phone;
    }

    public EmailAddress getEmailAddress() {
        return emailAddress;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private MemberId id;
        private String firstName;
        private String lastName;

        public Member build() {
            return new Member(id, firstName, lastName);
        }

        public Builder id(MemberId id) {
            this.id = id;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }
    }
}
