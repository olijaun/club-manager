package org.jaun.clubmanager.member.domain.model.member;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.junit.Test;

public class MemberIdTest {

    @Test
    public void equal() {
        ContactId contactId = new ContactId("1");
        MemberId memberId = new MemberId("1");

        assertThat(contactId, equalTo(memberId));
    }

    @Test
    public void nonEqual() {
        ContactId contactId = new ContactId("1");
        MemberId memberId = new MemberId("2");

        assertThat(contactId, not(equalTo(memberId)));
    }
}