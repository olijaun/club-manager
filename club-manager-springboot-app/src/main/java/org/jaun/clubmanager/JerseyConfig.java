package org.jaun.clubmanager;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

// https://stackoverflow.com/questions/29658240/spring-boot-jersey-allow-jersey-to-serve-static-content
@Component
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
//        register(EventStoreResource.class);
//        register(PersonResource.class);
//        register(MembershipTypeResource.class);
//        register(SubscriptionPeriodResource.class);
//        register(MemberResource.class);
    }
}
