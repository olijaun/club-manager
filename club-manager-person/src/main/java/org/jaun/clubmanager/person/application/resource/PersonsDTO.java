package org.jaun.clubmanager.person.application.resource;

import java.io.Serializable;
import java.util.Collection;

public class PersonsDTO implements Serializable {

    private Collection<PersonDTO> persons;

    public Collection<PersonDTO> getPersons() {
        return persons;
    }

    public void setPersons(Collection<PersonDTO> persons) {
        this.persons = persons;
    }
}
