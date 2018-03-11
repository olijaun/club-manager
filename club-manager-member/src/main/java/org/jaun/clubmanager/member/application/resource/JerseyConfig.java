package org.jaun.clubmanager.member.application.resource;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(MemberResource.class);
        register(ContactResource.class);
    }
}
