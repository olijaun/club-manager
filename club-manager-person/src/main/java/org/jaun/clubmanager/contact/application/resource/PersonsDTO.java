package org.jaun.clubmanager.contact.application.resource;

import java.io.Serializable;
import java.util.Collection;

public class PersonsDTO implements Serializable {

    private Collection<PersonDTO> persons;

    public Collection<PersonDTO> getContacts() {
        return persons;
    }

    public void setContacts(Collection<PersonDTO> persons) {
        this.persons = persons;
    }
}
