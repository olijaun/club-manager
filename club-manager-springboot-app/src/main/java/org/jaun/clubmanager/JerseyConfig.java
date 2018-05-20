package org.jaun.clubmanager;

import org.glassfish.jersey.server.ResourceConfig;
import org.jaun.clubmanager.contact.application.resource.ContactResource;
import org.jaun.clubmanager.invoice.application.resource.InvoiceResource;
import org.jaun.clubmanager.member.application.resource.MembershipTypeResource;
import org.jaun.clubmanager.member.application.resource.SubscriptionPeriodResource;
import org.jaun.clubmanager.member.application.resource.SubscriptionResource;
import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
        register(ContactResource.class);
        register(MembershipTypeResource.class);
        register(SubscriptionPeriodResource.class);
        register(SubscriptionResource.class);
        register(InvoiceResource.class);
    }
}
