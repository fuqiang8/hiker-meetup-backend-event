package com.aws.codestar.hikermeetup.member.data;

import com.aws.codestar.hikermeetup.event.data.Event;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Member {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToMany
    private List<Event> attended;

    @ManyToMany
    private List<Event> followed;

    private UUID externalIamId;

    private String name;

    public Member() {
    }

    public Member(UUID externalIamId, String name) {
        this.externalIamId = externalIamId;
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return id.equals(member.id) &&
                externalIamId.equals(member.externalIamId) &&
                name.equals(member.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
