package org.jaun.clubmanager.member.infra.service;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.jaun.clubmanager.member.domain.model.person.Person;
import org.jaun.clubmanager.member.domain.model.person.PersonId;
import org.jaun.clubmanager.member.domain.model.person.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClubManagerPersonService implements PersonService {

    private final WebTarget target;

    public ClubManagerPersonService(@Autowired WebTarget target) {
        this.target = target;
    }

    @Override
    public Person getPerson(PersonId personId) {
        try {
            PersonDTO personDTO = target.path(personId.getValue()).request(MediaType.APPLICATION_JSON).get(PersonDTO.class);
            return new Person(new PersonId(personDTO.getId()));

        } catch (NotFoundException e) {
            return null;
        }
    }
}
