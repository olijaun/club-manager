package org.jaun.clubmanager.invoice.infra.service;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.jaun.clubmanager.invoice.domain.model.contact.Contact;
import org.jaun.clubmanager.invoice.domain.model.contact.ContactId;
import org.jaun.clubmanager.invoice.domain.model.contact.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClubManagerInvoiceContactService implements ContactService {

    private final WebTarget target;

    public ClubManagerInvoiceContactService(@Autowired WebTarget target) {
        this.target = target;
    }

    @Override
    public Contact getContact(ContactId contactId) {
        try {
            ContactDTO contactDTO = target.path(contactId.getValue()).request(MediaType.APPLICATION_JSON).get(ContactDTO.class);
            return new Contact(new ContactId(contactDTO.getContactId()));

        } catch (NotFoundException e) {
            return null;
        }
    }
}
