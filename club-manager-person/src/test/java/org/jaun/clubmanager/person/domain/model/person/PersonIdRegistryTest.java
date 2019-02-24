package org.jaun.clubmanager.person.domain.model.person;

import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRegistryCreatedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.PersonIdRequestedEvent;
import org.jaun.clubmanager.person.domain.model.person.event.StartFromResetEvent;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

class PersonIdRegistryTest {

    @Test
    void construct() {

        // prepare
        int startFrom = 3;
        PersonIdRegistryId personIdRegistryId = new PersonIdRegistryId("test");

        // run
        PersonIdRegistry registry = new PersonIdRegistry(personIdRegistryId, startFrom);

        // verify values have been applied correctly to aggregate root
        assertThat(registry.getId(), equalTo(personIdRegistryId));
        assertThat(registry.getVersion(), equalTo(0));

        // verify events have been created
        assertThat(registry.getChanges().size(), equalTo(1));

        PersonIdRegistryCreatedEvent event = (PersonIdRegistryCreatedEvent) registry.getChanges().get(0);
        assertThat(event.getPersonIdRegistryId(), equalTo(personIdRegistryId));
        assertThat(event.getStartFrom(), equalTo(startFrom));
    }

    @Test
    void request() {

        // prepare
        int startFrom = 3;
        PersonIdRegistryId personIdRegistryId = new PersonIdRegistryId("test");
        PersonIdRegistry registry = new PersonIdRegistry(personIdRegistryId, startFrom);
        registry.clearChanges();

        PersonIdRequestId personIdRequestId = new PersonIdRequestId(UUID.fromString("7b1d3b20-3817-4cd0-be68-2080c5e64c6d"));

        // run
        registry.requestId(personIdRequestId);

        // verify values have been applied correctly to aggregate root
        PersonId generatedPersonId = registry.getPersonIdByRequestId(personIdRequestId).get();
        assertThat(generatedPersonId.getValue(), equalTo("P00000003"));

        // verify events have been created
        assertThat(registry.getChanges().size(), equalTo(1));

        PersonIdRequestedEvent event = (PersonIdRequestedEvent) registry.getChanges().get(0);
        assertThat(event.getPersonIdRegistryId(), equalTo(personIdRegistryId));
        assertThat(event.getPersonIdRequestId(), equalTo(personIdRequestId));
    }

    @Test
    void continueAfter() {

        // prepare
        int startFrom = 3;
        PersonIdRegistryId personIdRegistryId = new PersonIdRegistryId("test");
        PersonIdRegistry registry = new PersonIdRegistry(personIdRegistryId, startFrom);
        registry.clearChanges();

        // run
        registry.continueAfter(new PersonId("P00000042"));

        // verify events have been created
        assertThat(registry.getChanges().size(), equalTo(1));

        StartFromResetEvent event = (StartFromResetEvent) registry.getChanges().get(0);
        assertThat(event.getNewStartFrom(), equalTo(43));
    }

    @Test
    void continueAfter_continueAfterAndThenRequest() {

        // prepare
        int startFrom = 3;
        PersonIdRegistryId personIdRegistryId = new PersonIdRegistryId("test");
        PersonIdRegistry registry = new PersonIdRegistry(personIdRegistryId, startFrom);
        registry.clearChanges();

        PersonIdRequestId personIdRequestId = new PersonIdRequestId(UUID.fromString("7b1d3b20-3817-4cd0-be68-2080c5e64c6d"));

        // run
        registry.continueAfter(new PersonId("P00000042"));
        registry.requestId(personIdRequestId);

        // verify events have been created
        PersonId generatedPersonId = registry.getPersonIdByRequestId(personIdRequestId).get();
        assertThat(generatedPersonId.getValue(), equalTo("P00000043"));
    }
}