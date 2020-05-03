package com.aws.codestar.hikermeetup.member.data;

import com.aws.codestar.hikermeetup.event.data.Event;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Member {
    private UUID id;

    private List<Event> attended;

    private List<Event> followed;

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

    public UUID getId() {
        return id;
    }

    public List<Event> getAttended() {
        return attended;
    }

    public void setAttended(List<Event> attended) {
        this.attended = attended;
    }

    public List<Event> getFollowed() {
        return followed;
    }

    public void setFollowed(List<Event> followed) {
        this.followed = followed;
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
        return id.equals(member.id) &&
                externalIamId.equals(member.externalIamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
