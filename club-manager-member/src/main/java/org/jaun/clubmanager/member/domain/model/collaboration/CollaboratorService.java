package org.jaun.clubmanager.member.domain.model.collaboration;

public interface CollaboratorService {

    Admin adminFrom(CollaboratorId id);

    BoardMember boardMemberFrom(CollaboratorId id);
}
