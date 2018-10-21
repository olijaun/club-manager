package org.jaun.clubmanager;

import org.glassfish.jersey.server.ResourceConfig;
import org.jaun.clubmanager.eventstore.EventStoreResource;
import org.jaun.clubmanager.member.application.resource.MemberResource;
import org.jaun.clubmanager.member.application.resource.MembershipTypeResource;
import org.jaun.clubmanager.member.application.resource.SubscriptionPeriodResource;
import org.jaun.clubmanager.person.application.resource.PersonResource;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(EventStoreResource.class);
        register(PersonResource.class);
        register(MembershipTypeResource.class);
        register(SubscriptionPeriodResource.class);
        register(MemberResource.class);
    }
}
