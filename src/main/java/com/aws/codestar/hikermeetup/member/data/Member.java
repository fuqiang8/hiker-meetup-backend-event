package com.aws.codestar.hikermeetup.member.data;


import java.util.Objects;
import java.util.UUID;

public class Member {

    private UUID externalIamId;

    private String name;

    private String email;

    public Member() {
    }

    public Member(UUID externalIamId, String name, String email) {
        this.externalIamId = externalIamId;
        this.name = name;
        this.email = email;
    }

    public UUID getExternalIamId() {
        return externalIamId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return externalIamId.equals(member.externalIamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(externalIamId);
    }
}
