package org.jaun.clubmanager.member.domain.model.membership;

import java.util.Collection;

public interface MembershipPeriodRepository {

    Collection<MembershipPeriod> getAll();
}
