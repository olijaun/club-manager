package org.jaun.clubmanager.contact.application.resource;

import java.io.Serializable;
import java.util.Collection;

public class ContactsDTO implements Serializable {

    private Collection<ContactDTO> contacts;

    public Collection<ContactDTO> getContacts() {
        return contacts;
    }

    public void setContacts(Collection<ContactDTO> contacts) {
        this.contacts = contacts;
    }
}
