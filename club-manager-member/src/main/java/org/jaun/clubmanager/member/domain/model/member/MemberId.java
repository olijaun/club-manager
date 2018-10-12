package org.jaun.clubmanager.member.domain.model.member;

import org.jaun.clubmanager.member.domain.model.person.PersonId;

public class MemberId extends PersonId {
    public MemberId(String value) {
        super(value);
    }

    public static MemberId from(PersonId personId) {
        return new MemberId(personId.getValue());
    }
}
