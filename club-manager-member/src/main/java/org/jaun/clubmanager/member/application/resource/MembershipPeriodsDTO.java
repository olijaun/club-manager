package org.jaun.clubmanager.member.application.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MembershipPeriodsDTO implements Serializable {
    private ArrayList<MembershipPeriodDTO> membershipPeriods = new ArrayList<>();

    public List<MembershipPeriodDTO> getMembershipPeriods() {
        return membershipPeriods;
    }

    public void setMembershipPeriods(Collection<MembershipPeriodDTO> membershipPeriodDTOS) {
        if (membershipPeriodDTOS != null) {
            this.membershipPeriods = new ArrayList<>(membershipPeriodDTOS);
        }
    }
}
