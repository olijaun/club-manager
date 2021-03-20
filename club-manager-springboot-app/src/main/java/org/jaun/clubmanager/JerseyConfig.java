package org.jaun.clubmanager;

import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.server.ResourceConfig;
import org.jaun.clubmanager.masterdata.application.resource.MasterDataResource;
import org.jaun.clubmanager.member.application.resource.MemberResource;
import org.jaun.clubmanager.member.application.resource.MembersDTOCsvWriter;
import org.jaun.clubmanager.member.application.resource.MembershipTypeResource;
import org.jaun.clubmanager.member.application.resource.SubscriptionPeriodResource;
import org.jaun.clubmanager.person.application.resource.PersonIdRequestResource;
import org.jaun.clubmanager.person.application.resource.PersonResource;
import org.jaun.clubmanager.person.application.resource.PersonsDTOCsvWriter;
import org.springframework.stereotype.Component;

// https://stackoverflow.com/questions/29658240/spring-boot-jersey-allow-jersey-to-serve-static-content
@Component
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig {

    // checkout https://psamsotha.github.io/jersey/2015/10/18/http-caching-with-jersey.html for some caching stuff

    public JerseyConfig() {

        register(JerseyObjectMapperProvider.class);

        // custom writer
        register(PersonsDTOCsvWriter.class);
        register(MembersDTOCsvWriter.class);

        // resources
        register(MasterDataResource.class);
        register(PersonResource.class);
        register(PersonIdRequestResource.class);
        register(MembershipTypeResource.class);
        register(SubscriptionPeriodResource.class);
        register(MemberResource.class);
    }
}
