package com.aws.codestar.hikermeetup.event.data;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.aws.codestar.hikermeetup.base.dynamodb.LocalDateTimeConverter;
import com.aws.codestar.hikermeetup.member.data.Member;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@DynamoDBTable(tableName = "Events")
public class Event {
    private UUID id;

    private Member organizer;

    // Sign up
    private List<Member> attendees;

    // Interest
    private List<Member> followers;

    @NotNull
    private EventStatus eventStatus;

    @NotNull
    private Integer minAttendees;

    private String category;

    @NotNull
    private String name;

    @NotNull
    private String location;

    private String description;

    @NotNull
    private LocalDateTime startDateTime;

    @NotNull
    private LocalDateTime endDateTime;

    public Event() {
    }

    public Event(Member organizer) {
        this.organizer = organizer;
    }

    @DynamoDBHashKey(attributeName="eventId")
    @DynamoDBAutoGeneratedKey
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @DynamoDBAttribute
    public Member getOrganizer() {
        return organizer;
    }

    public void setOrganizer(Member organizer) {
        this.organizer = organizer;
    }

    @DynamoDBAttribute
    public List<Member> getAttendees() {
        return attendees;
    }

    public void setAttendees(List<Member> attendees) {
        this.attendees = attendees;
    }

    @DynamoDBAttribute
    public List<Member> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Member> followers) {
        this.followers = followers;
    }

    @DynamoDBTypeConverted(converter = EventStatusConverter.class)
    public EventStatus getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(EventStatus eventStatus) {
        this.eventStatus = eventStatus;
    }

    @DynamoDBAttribute
    public Integer getMinAttendees() {
        return minAttendees;
    }

    public void setMinAttendees(Integer minAttendees) {
        this.minAttendees = minAttendees;
    }

    @DynamoDBAttribute
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @DynamoDBAttribute
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @DynamoDBAttribute
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}
