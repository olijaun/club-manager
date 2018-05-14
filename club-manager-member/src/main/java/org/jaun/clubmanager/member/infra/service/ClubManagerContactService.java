package org.jaun.clubmanager.member.infra.service;

import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactService;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.springframework.stereotype.Service;

@Service
public class ClubManagerContactService implements ContactService {

    @Override
    public Contact getContact(ContactId contactId) {

        return new Contact(contactId, "firstName", "lastName");
    }
}
