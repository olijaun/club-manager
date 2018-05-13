package org.jaun.clubmanager;

import org.glassfish.jersey.server.ResourceConfig;
import org.jaun.clubmanager.member.application.resource.ContactResource;
import org.jaun.clubmanager.member.application.resource.MembershipPeriodResource;
import org.jaun.clubmanager.member.application.resource.MembershipTypeResource;
import org.jaun.clubmanager.member.application.resource.MembershipResource;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(ContactResource.class);
        register(MembershipTypeResource.class);
        register(MembershipPeriodResource.class);
        register(MembershipResource.class);
    }
}
