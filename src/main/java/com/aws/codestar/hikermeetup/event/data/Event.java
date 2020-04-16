package com.aws.codestar.hikermeetup.event.data;

import com.aws.codestar.hikermeetup.member.data.Member;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
public class Event {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private Member organizer;

    // Sign up
    @ManyToMany
    private Set<Member> attendees;

    // Interest
    @ManyToMany
    private Set<Member> followers;

    @Enumerated(EnumType.ORDINAL)
    @NotNull
    private EventStatus eventStatus;

    @NotNull
    private Integer minAttendees;

    private String category;

    @NotNull
    private String name;

    @NotNull
    private String location;

    @NotNull
    private LocalDateTime startDateTime;

    @NotNull
    private LocalDateTime endDateTime;

    public UUID getId() {
        return id;
    }

    public Member getOrganizer() {
        return organizer;
    }

    public Set<Member> getAttendees() {
        return attendees;
    }

    public void setAttendees(Set<Member> attendees) {
        this.attendees = attendees;
    }

    public Set<Member> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<Member> followers) {
        this.followers = followers;
    }

    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Integer getMinAttendees() {
        return minAttendees;
    }

    public void setMinAttendees(Integer minAttendees) {
        this.minAttendees = minAttendees;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}
