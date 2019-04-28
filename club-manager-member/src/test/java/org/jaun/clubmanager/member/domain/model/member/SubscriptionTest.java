package org.jaun.clubmanager.member.domain.model.member;

import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionFixture;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionPeriodId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionTypeId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class SubscriptionTest {

    @Test
    void construct() {

        // run
        Subscription subscription = SubscriptionFixture.subscription().build();
        // verify
        assertThat(subscription.getId(), equalTo(new SubscriptionId("subscriptionA")));
    }

    @Test
    void construct_null() {

        // run
        Executable e1 = () -> SubscriptionFixture.subscription().id(null).build();
        Executable e2 = () -> SubscriptionFixture.subscription().subscriptionTypeId(null).build();
        Executable e3 = () -> SubscriptionFixture.subscription().additionalMemberIds(null).build();

        // verify
        assertThrows(NullPointerException.class, e1);
        assertThrows(NullPointerException.class, e2);
        assertThrows(NullPointerException.class, e3);
    }

    @Test
    void matchesPeriodAndType() {

        // prepare
        Subscription subscription = SubscriptionFixture.subscription().build();

        // run
        boolean matchesPeriodAndType = subscription.matchesPeriodAndType(subscription.getSubscriptionPeriodId(), subscription.getSubscriptionTypeId());

        // verify
        assertThat(matchesPeriodAndType, is(true));
    }

    @Test
    void matchesPeriodAndType_noMatch_bothDoNotMatch() {

        // prepare
        Subscription subscription = SubscriptionFixture.subscription().build();

        // run
        boolean matchesPeriodAndType = subscription.matchesPeriodAndType(new SubscriptionPeriodId("blablabla12345"), new SubscriptionTypeId("alsdfsdfsdf213"));

        // verify
        assertThat(matchesPeriodAndType, is(false));
    }

    @Test
    void matchesPeriodAndType_noMatch_onlyPeriodIdMatches() {

        // prepare
        Subscription subscription = SubscriptionFixture.subscription().build();

        // run
        boolean matchesPeriodAndType = subscription.matchesPeriodAndType(subscription.getSubscriptionPeriodId(), new SubscriptionTypeId("alsdfsdfsdf213"));

        // verify
        assertThat(matchesPeriodAndType, is(false));
    }

    @Test
    void matchesPeriodAndType_noMatch_onlySubscriptionTypeIdMatches() {

        // prepare
        Subscription subscription = SubscriptionFixture.subscription().build();

        // run
        boolean matchesPeriodAndType = subscription.matchesPeriodAndType(new SubscriptionPeriodId("blablabla12345"), subscription.getSubscriptionTypeId());

        // verify
        assertThat(matchesPeriodAndType, is(false));
    }
}