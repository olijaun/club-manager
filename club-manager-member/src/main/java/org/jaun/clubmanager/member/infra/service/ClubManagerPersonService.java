package org.jaun.clubmanager.member.infra.service;

import org.jaun.clubmanager.member.domain.model.person.Person;
import org.jaun.clubmanager.member.domain.model.person.PersonId;
import org.jaun.clubmanager.member.domain.model.person.PersonService;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class ClubManagerPersonService implements PersonService {

    private final WebTarget target;

    public ClubManagerPersonService(WebTarget target) {
        this.target = target;
    }

    @Override
    public Person getPerson(PersonId personId) {
        try {
            PersonDTO personDTO = target.path(personId.getValue()).request(MediaType.APPLICATION_JSON).get(PersonDTO.class);
            return new Person(new PersonId(personDTO.getId()));

        } catch (NotFoundException e) {
            return null;
        } catch(Exception e) {
            throw new IllegalStateException("failed to call person service", e);
        }
    }
}
