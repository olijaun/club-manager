package org.jaun.clubmanager.member.domain.model.collaboration;

import org.jaun.clubmanager.identity.domain.model.Role;
import org.jaun.clubmanager.identity.domain.model.User;
import org.jaun.clubmanager.identity.domain.model.UserApplicationServiceBean;
import org.jaun.clubmanager.identity.domain.model.UserId;

import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;

@Stateless
@Local(CollaboratorService.class)
public class LocalBeanCollaboratorService implements CollaboratorService {

    @EJB
    private UserApplicationServiceBean userApplicationServiceBean;

    @Override
    public Admin adminFrom(CollaboratorId id) {
        User user = userApplicationServiceBean.getUser(new UserId(id.getValue()));

        if (user.isUserInRole(Role.ADMIN)) {
            return new Admin(new CollaboratorId(user.getId().getValue()), user.getName());
        } else {
            throw new IllegalStateException("collaborator " + id + " is not an admin");
        }
    }

    @Override
    public BoardMember boardMemberFrom(CollaboratorId id) {
        User user = userApplicationServiceBean.getUser(new UserId(id.getValue()));

        if (user.isUserInRole(Role.BOARD_MEMBER)) {
            return new BoardMember(new CollaboratorId(user.getId().getValue()), user.getName());
        } else {
            throw new IllegalStateException("collaborator " + id + " is not an admin");
        }
    }
}
