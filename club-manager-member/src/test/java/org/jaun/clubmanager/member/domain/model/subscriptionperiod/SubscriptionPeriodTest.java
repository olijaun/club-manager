package org.jaun.clubmanager.member.domain.model.subscriptionperiod;

import org.jaun.clubmanager.member.domain.model.member.SubscriptionId;
import org.jaun.clubmanager.member.domain.model.membershiptype.MembershipTypeId;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionPeriodCreatedEvent;
import org.jaun.clubmanager.member.domain.model.subscriptionperiod.event.SubscriptionTypeAddedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Currency;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SubscriptionPeriodTest {

    @Test
    void create() {

        // prepare
        SubscriptionPeriodId subscriptionPeriodId = new SubscriptionPeriodId("periodA");
        LocalDate start = LocalDate.now().minus(1, ChronoUnit.YEARS);
        LocalDate end = LocalDate.now().plus(1, ChronoUnit.YEARS);

        // run
        SubscriptionPeriod subscriptionPeriod = new SubscriptionPeriod(subscriptionPeriodId, start, end);

        // verify
        assertThat(subscriptionPeriod.getId(), equalTo(subscriptionPeriodId));
        assertThat(subscriptionPeriod.getStart(), equalTo(start));
        assertThat(subscriptionPeriod.getEnd(), equalTo(end));

        // verify events
        assertThat(subscriptionPeriod.getChanges().size(), is(1));

        SubscriptionPeriodCreatedEvent subscriptionPeriodCreatedEvent = (SubscriptionPeriodCreatedEvent) subscriptionPeriod.getChanges().get(0);
        assertThat(subscriptionPeriodCreatedEvent.getSubscriptionPeriodId(), equalTo(subscriptionPeriodId));
        assertThat(subscriptionPeriodCreatedEvent.getStart(), equalTo(start));
        assertThat(subscriptionPeriodCreatedEvent.getEnd(), equalTo(end));
    }

    @Test
    void addSubscriptionType() {

        // prepare
        SubscriptionPeriodId subscriptionPeriodId = new SubscriptionPeriodId("periodA");
        LocalDate start = LocalDate.now().minus(1, ChronoUnit.YEARS);
        LocalDate end = LocalDate.now().plus(1, ChronoUnit.YEARS);

        SubscriptionPeriod subscriptionPeriod = new SubscriptionPeriod(subscriptionPeriodId, start, end);
        subscriptionPeriod.clearChanges();

        SubscriptionTypeId subscriptionTypeId = new SubscriptionTypeId("subTypeA");
        MembershipTypeId membershipTypeId = new MembershipTypeId("memTypeA");
        String name = "Sub Type A";
        Money price = new Money(60, Currency.getInstance("CHF"));
        int maxSubscribers = 1;

        // run
        subscriptionPeriod.addSubscriptionType(subscriptionTypeId, membershipTypeId, name, price, maxSubscribers);

        // verify
        assertThat(subscriptionPeriod.getId(), equalTo(subscriptionPeriodId));
        assertThat(subscriptionPeriod.getSubscriptionTypes().size(), is(1));

        SubscriptionType subscriptionType = subscriptionPeriod.getSubscriptionTypes().iterator().next();

        assertThat(subscriptionType.getId(), equalTo(subscriptionTypeId));
        assertThat(subscriptionType.getName(), equalTo(name));
        assertThat(subscriptionType.getPrice(), equalTo(price));
        assertThat(subscriptionType.getMaxSubscribers(), is(maxSubscribers));

        // verify events
        assertThat(subscriptionPeriod.getChanges().size(), is(1));

        SubscriptionTypeAddedEvent subscriptionTypeAddedEvent = (SubscriptionTypeAddedEvent) subscriptionPeriod.getChanges().get(0);
        assertThat(subscriptionTypeAddedEvent.getSubscriptionTypeId(), equalTo(subscriptionTypeId));
        assertThat(subscriptionTypeAddedEvent.getName(), equalTo(name));
        assertThat(subscriptionTypeAddedEvent.getPrice(), equalTo(price));
        assertThat(subscriptionTypeAddedEvent.getMaxSubscribers(), is(maxSubscribers));
    }

    /**
     * It must not be possible to add the same subscription type twice. We just ignore the second one.
     */
    @Test
    void addSubscriptionType_addTwiceIgnored() {

        // prepare
        SubscriptionPeriodId subscriptionPeriodId = new SubscriptionPeriodId("periodA");
        LocalDate start = LocalDate.now().minus(1, ChronoUnit.YEARS);
        LocalDate end = LocalDate.now().plus(1, ChronoUnit.YEARS);

        SubscriptionPeriod subscriptionPeriod = new SubscriptionPeriod(subscriptionPeriodId, start, end);
        subscriptionPeriod.clearChanges();

        SubscriptionTypeId subscriptionTypeId = new SubscriptionTypeId("subTypeA");
        MembershipTypeId membershipTypeId = new MembershipTypeId("memTypeA");
        String name = "Sub Type A";
        Money price = new Money(60, Currency.getInstance("CHF"));
        int maxSubscribers = 1;

        // run
        subscriptionPeriod.addSubscriptionType(subscriptionTypeId, membershipTypeId, name, price, maxSubscribers);
        subscriptionPeriod.addSubscriptionType(subscriptionTypeId, membershipTypeId, name, price, maxSubscribers);

        // verify
        assertThat(subscriptionPeriod.getId(), equalTo(subscriptionPeriodId));
        assertThat(subscriptionPeriod.getSubscriptionTypes().size(), is(1));

        // verify events
        assertThat(subscriptionPeriod.getChanges().size(), is(1));
    }

    @Test
    void createSubscriptionRequest() throws Exception {

        // prepare
        SubscriptionPeriodId subscriptionPeriodId = new SubscriptionPeriodId("periodA");
        LocalDate start = LocalDate.now().minus(1, ChronoUnit.YEARS);
        LocalDate end = LocalDate.now().plus(1, ChronoUnit.YEARS);

        SubscriptionPeriod subscriptionPeriod = new SubscriptionPeriod(subscriptionPeriodId, start, end);
        SubscriptionId subscriptionId = new SubscriptionId("sub1");

        SubscriptionTypeId subscriptionTypeId = new SubscriptionTypeId("subTypeA");
        MembershipTypeId membershipTypeId = new MembershipTypeId("memTypeA");
        String name = "Sub Type A";
        Money price = new Money(60, Currency.getInstance("CHF"));
        int maxSubscribers = 1;

        subscriptionPeriod.addSubscriptionType(subscriptionTypeId, membershipTypeId, name, price, maxSubscribers);

        // run
        SubscriptionRequest subscriptionRequest = subscriptionPeriod.createSubscriptionRequest(subscriptionId, subscriptionTypeId, Collections.emptyList());

        // verify
        assertThat(subscriptionRequest.getId(), equalTo(subscriptionId));
        assertThat(subscriptionRequest.getAdditionalSubscriberIds().size(), is(0));
        assertThat(subscriptionRequest.getSubscriptionPeriodId(), is(subscriptionPeriodId));
        assertThat(subscriptionRequest.getSubscriptionTypeId(), is(subscriptionTypeId));

    }

    @Test
    void createSubscriptionRequest_NoSuchSubscriptionTypeForPeriod() throws Exception {

        // prepare
        SubscriptionPeriodId subscriptionPeriodId = new SubscriptionPeriodId("periodA");
        LocalDate start = LocalDate.now().minus(1, ChronoUnit.YEARS);
        LocalDate end = LocalDate.now().plus(1, ChronoUnit.YEARS);

        SubscriptionPeriod subscriptionPeriod = new SubscriptionPeriod(subscriptionPeriodId, start, end);
        SubscriptionId subscriptionId = new SubscriptionId("sub1");

        SubscriptionTypeId subscriptionTypeId = new SubscriptionTypeId("subTypeA");
        MembershipTypeId membershipTypeId = new MembershipTypeId("memTypeA");
        String name = "Sub Type A";
        Money price = new Money(60, Currency.getInstance("CHF"));
        int maxSubscribers = 1;

        subscriptionPeriod.addSubscriptionType(subscriptionTypeId, membershipTypeId, name, price, maxSubscribers);


        SubscriptionTypeId nonExistingSubscriptionTypeId = new SubscriptionTypeId("subTypeX");

        // run
        Executable e = () -> subscriptionPeriod.createSubscriptionRequest(subscriptionId, nonExistingSubscriptionTypeId, Collections.emptyList());

        // verify
        assertThrows(NoSuchSubscriptionTypeForPeriod.class, e);
    }

}