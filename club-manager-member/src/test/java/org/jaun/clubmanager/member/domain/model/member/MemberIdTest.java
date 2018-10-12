package org.jaun.clubmanager.member.domain.model.member;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import org.jaun.clubmanager.member.domain.model.person.PersonId;
import org.junit.Test;

public class MemberIdTest {

    @Test
    public void equal() {
        PersonId personId = new PersonId("1");
        MemberId memberId = new MemberId("1");

        assertThat(personId, equalTo(memberId));
    }

    @Test
    public void nonEqual() {
        PersonId personId = new PersonId("1");
        MemberId memberId = new MemberId("2");

        assertThat(personId, not(equalTo(memberId)));
    }
}