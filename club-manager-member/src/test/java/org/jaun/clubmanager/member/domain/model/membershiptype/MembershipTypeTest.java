package org.jaun.clubmanager.member.domain.model.membershiptype;

import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeCreatedEvent;
import org.jaun.clubmanager.member.domain.model.membershiptype.event.MembershipTypeMetadataChangedEvent;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MembershipTypeTest {

    @Test
    void create() {

        // prepare
        MembershipTypeId membershipTypeId = new MembershipTypeId("typeA");
        String name = "Type A";
        String description = "desc";

        // run
        MembershipType membershipType = new MembershipType(membershipTypeId, name, description);

        // verify
        assertThat(membershipType.getId(), equalTo(membershipTypeId));
        assertThat(membershipType.getName(), equalTo(name));
        assertThat(membershipType.getDescription().get(), equalTo(description));

        // verify events
        assertThat(membershipType.getChanges().size(), is(2));

        MembershipTypeCreatedEvent membershipTypeCreatedEvent = (MembershipTypeCreatedEvent) membershipType.getChanges().get(0);
        assertThat(membershipTypeCreatedEvent.getMembershipTypeId(), equalTo(membershipTypeId));

        MembershipTypeMetadataChangedEvent membershipTypeMetadataChangedEvent = (MembershipTypeMetadataChangedEvent) membershipType.getChanges().get(1);
        assertThat(membershipTypeMetadataChangedEvent.getMembershipTypeId(), equalTo(membershipTypeId));
        assertThat(membershipTypeMetadataChangedEvent.getName(), equalTo(name));
        assertThat(membershipTypeMetadataChangedEvent.getDescription().get(), equalTo(description));
    }
}