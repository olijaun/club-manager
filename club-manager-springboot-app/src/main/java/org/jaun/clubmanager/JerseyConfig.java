package org.jaun.clubmanager;

import org.glassfish.jersey.server.ResourceConfig;
import org.jaun.clubmanager.eventstore.EventStoreResource;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(EventStoreResource.class);
//        register(ContactResource.class);
//        register(MembershipTypeResource.class);
//        register(SubscriptionPeriodResource.class);
//        register(SubscriptionResource.class);
//        register(InvoiceResource.class);
    }
}
