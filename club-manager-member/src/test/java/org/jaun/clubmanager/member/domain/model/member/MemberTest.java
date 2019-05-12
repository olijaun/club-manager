package org.jaun.clubmanager.member.domain.model.member;

import org.jaun.clubmanager.member.domain.model.member.event.MemberCreatedEvent;
import org.jaun.clubmanager.member.domain.model.member.event.SubscriptionCreatedEvent;
import org.jaun.clubmanager.member.domain.model.member.event.SubscriptionDeletedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionRequest;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.SubscriptionRequestFixture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
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

    @Test
    void construct_null() {
        Executable e1 = () -> MemberFixture.newMember(null);
        assertThrows(NullPointerException.class, e1);
    }

    @Test
    void subscribe() throws Exception {

        // prepare
        Member member = MemberFixture.newMember();
        member.clearChanges();

        SubscriptionRequest subscriptionRequest = SubscriptionRequestFixture.subscriptionRequest().build();

        // run
        member.subscribe(subscriptionRequest);

        // verify values have been applied correctly to aggregate root
        assertThat(member.getId().getValue(), equalTo("P12345678"));
        assertThat(member.getSubscriptions().size(), equalTo(1));

        assertThat(member.getSubscriptions().size(), is(1));
        Subscription subscription = member.getSubscriptions().first().get();
        assertThat(subscription.getId(), equalTo(subscriptionRequest.getId()));
        assertThat(subscription.getMemberId(), equalTo(member.getId()));
        assertThat(subscription.getSubscriptionPeriodId(), equalTo(subscriptionRequest.getSubscriptionPeriodId()));
        assertThat(subscription.getSubscriptionTypeId(), equalTo(subscriptionRequest.getSubscriptionTypeId()));
        assertThat(subscription.getAdditionalMemberIds().size(), is(0));

        // verify event has been created
        assertThat(member.getChanges().size(), equalTo(1));

        SubscriptionCreatedEvent subscriptionCreatedEvent = (SubscriptionCreatedEvent) member.getChanges().get(0);
        assertThat(subscriptionCreatedEvent.getSubscriptionId(), equalTo(subscriptionRequest.getId()));
        assertThat(subscriptionCreatedEvent.getMemberId(), equalTo(member.getId()));
        assertThat(subscriptionCreatedEvent.getSubscriptionPeriodId(), equalTo(subscriptionRequest.getSubscriptionPeriodId()));
        assertThat(subscriptionCreatedEvent.getSubscriptionTypeId(), equalTo(subscriptionRequest.getSubscriptionTypeId()));
        assertThat(subscriptionCreatedEvent.getAdditionalSubscriberIds().size(), is(0));
    }

    /**
     * The second request with same id must be ignored.
     */
    @Test
    void subscribe_twiceWithSameId() throws Exception {

        // prepare
        Member member = MemberFixture.newMember();
        member.clearChanges();

        SubscriptionRequest subscriptionRequest = SubscriptionRequestFixture.subscriptionRequest().subscriptionId(new SubscriptionId("idA")).build();

        // run
        member.subscribe(subscriptionRequest);
        member.subscribe(subscriptionRequest);

        // verify
        assertThat(member.getSubscriptions().size(), is(1));

        // verify values have been applied correctly to aggregate root
        Subscription subscription = member.getSubscriptions().first().get();
        assertThat(subscription.getId(), equalTo(subscriptionRequest.getId()));

        // verify event has been created
        assertThat(member.getChanges().size(), equalTo(1));
    }

    /**
     * The second request must be rejected because it referes to the same period and same subscription type
     */
    @Test
    void subscribe_twoRequestForSamePeriod() throws Exception {

        // prepare
        Member member = MemberFixture.newMember();
        member.clearChanges();

        SubscriptionRequest subscriptionRequestA = SubscriptionRequestFixture.subscriptionRequest().subscriptionId(new SubscriptionId("idA")).build();
        SubscriptionRequest subscriptionRequestB = SubscriptionRequestFixture.subscriptionRequest().subscriptionId(new SubscriptionId("idB")).build();

        member.subscribe(subscriptionRequestA);

        // run
        Executable e = () -> member.subscribe(subscriptionRequestB);

        // verify
        assertThrows(SubscriptionOfSamePeriodAndTypeExistsException.class, e);

        // verify values have been applied correctly to aggregate root
        Subscription subscription = member.getSubscriptions().first().get();
        assertThat(subscription.getId(), equalTo(subscriptionRequestA.getId()));

        // verify event has been created
        assertThat(member.getChanges().size(), equalTo(1));

        SubscriptionCreatedEvent subscriptionCreatedEvent = (SubscriptionCreatedEvent) member.getChanges().get(0);
        assertThat(subscriptionCreatedEvent.getSubscriptionId(), equalTo(subscriptionRequestA.getId()));
    }

    @Test
    void deleteSubscription() throws Exception {

        // prepare
        Member member = MemberFixture.newMember();

        SubscriptionRequest subscriptionRequest = SubscriptionRequestFixture.subscriptionRequest().build();
        member.subscribe(subscriptionRequest);

        member.clearChanges();

        // run
        member.deleteSubscription(subscriptionRequest.getId());

        // verify values have been applied correctly to aggregate root
        assertThat(member.getSubscriptions().size(), is(0));

        // verify event has been created
        assertThat(member.getChanges().size(), equalTo(1));

        SubscriptionDeletedEvent subscriptionDeletedEvent = (SubscriptionDeletedEvent) member.getChanges().get(0);
        assertThat(subscriptionDeletedEvent.getSubscriptionId(), equalTo(subscriptionRequest.getId()));
        assertThat(subscriptionDeletedEvent.getMemberId(), equalTo(member.getId()));
    }
}