package com.aws.codestar.hikermeetup.member.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import java.util.Objects;
import java.util.UUID;

@DynamoDBDocument
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

    @DynamoDBAttribute
    public UUID getExternalIamId() {
        return externalIamId;
    }

    public void setExternalIamId(UUID externalIamId) {
        this.externalIamId = externalIamId;
    }

    @DynamoDBAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
