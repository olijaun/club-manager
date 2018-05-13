package org.jaun.clubmanager;

import org.glassfish.jersey.server.ResourceConfig;
import org.jaun.clubmanager.member.application.resource.ContactResource;
import org.jaun.clubmanager.member.application.resource.MembershipsResource;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(ContactResource.class);
        register(MembershipsResource.class);
    }
}
