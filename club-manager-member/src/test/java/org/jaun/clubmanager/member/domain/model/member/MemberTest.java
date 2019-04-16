package org.jaun.clubmanager.member.domain.model.member;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

import org.jaun.clubmanager.member.domain.model.member.event.MemberCreatedEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MemberTest {

    @Test
    void construct() {

        // run
        Member member = MemberFixture.newMember();

        // verify values have been applied correctly to aggregate root
        assertThat(member.getId().getValue(), equalTo("P12345678"));
        assertThat(member.getSubscriptions().size(), equalTo(0));

        // verify events have been created
        assertThat(member.getChanges().size(), equalTo(1));

        MemberCreatedEvent memberCreatedEvent = (MemberCreatedEvent) member.getChanges().get(0);
        assertThat(memberCreatedEvent.getMemberId(), equalTo(member.getId()));
    }
}