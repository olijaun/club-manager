package org.jaun.clubmanager.person.domain.model.person;

import java.util.UUID;

import org.jaun.clubmanager.domain.model.commons.Id;

public class PersonIdRequestId extends Id {
    public PersonIdRequestId(UUID value) {
        super(value.toString());
    }
}
