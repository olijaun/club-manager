package org.jaun.clubmanager.member.application.resource;

public class MembershipDTO extends CreateMembershipDTO {

    private String membershipId;

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }
}
