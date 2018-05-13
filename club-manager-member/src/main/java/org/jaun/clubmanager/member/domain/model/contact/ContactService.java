package org.jaun.clubmanager.member.domain.model.contact;

import org.jaun.clubmanager.member.infra.projection.event.contact.ContactId;

public interface ContactService {

    Contact getContact(ContactId contactId);
}
