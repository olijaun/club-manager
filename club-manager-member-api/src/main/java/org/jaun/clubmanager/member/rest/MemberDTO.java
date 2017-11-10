package org.jaun.clubmanager.member.rest;

public class MemberDTO {
    private String memberId;
    private String memberFirstName;
    private String memberLastName;

    public String getMemberId() {
        return memberId;
    }

    public MemberDTO setMemberId(String memberId) {
        this.memberId = memberId;
        return this;
    }

    public String getMemberFirstName() {
        return memberFirstName;
    }

    public MemberDTO setMemberFirstName(String memberFirstName) {
        this.memberFirstName = memberFirstName;
        return this;
    }

    public String getMemberLastName() {
        return memberLastName;
    }

    public MemberDTO setMemberLastName(String memberLastName) {
        this.memberLastName = memberLastName;
        return this;
    }
}
