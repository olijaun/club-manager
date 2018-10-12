package org.jaun.clubmanager.member.infra.service;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.jaun.clubmanager.member.domain.model.contact.Contact;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;
import org.jaun.clubmanager.member.domain.model.contact.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClubManagerContactService implements ContactService {

    private final WebTarget target;

    public ClubManagerContactService(@Autowired WebTarget target) {
        this.target = target;
    }

    @Override
    public Contact getContact(ContactId contactId) {
        try {
            PersonDTO personDTO = target.path(contactId.getValue()).request(MediaType.APPLICATION_JSON).get(PersonDTO.class);
            return new Contact(new ContactId(personDTO.getId()));

        } catch (NotFoundException e) {
            return null;
        }
    }
}
