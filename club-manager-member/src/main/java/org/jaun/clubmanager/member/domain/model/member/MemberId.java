package org.jaun.clubmanager.member.domain.model.member;

import org.jaun.clubmanager.domain.model.commons.Id;
import org.jaun.clubmanager.member.domain.model.contact.ContactId;

public class MemberId extends Id {
    public MemberId(String value) {
        super(value);
    }

    public static MemberId from(ContactId contactId) {
        return new MemberId(contactId.getValue());
    }
}
