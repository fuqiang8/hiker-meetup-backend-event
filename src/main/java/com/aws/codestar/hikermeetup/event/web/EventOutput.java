package com.aws.codestar.hikermeetup.event.web;

import com.aws.codestar.hikermeetup.event.data.EventStatus;
import com.aws.codestar.hikermeetup.member.web.MemberOutput;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EventOutput {
    UUID getId();

    MemberOutput getOrganizer();

    @Value("#{target.getAttendees() ?: T(java.util.Collections).EMPTY_LIST}")
    List<MemberOutput> getAttendees();

    @Value("#{target.getFollowers() ?: T(java.util.Collections).EMPTY_LIST}")
    List<MemberOutput> getFollowers();

    EventStatus getEventStatus();

    Integer getMinAttendees();

    @Value("#{target.getCategory() ?: ''}")
    String getCategory();

    String getName();

    String getLocation();

    @Value("#{target.getDescription() ?: ''}")
    String getDescription();

    LocalDateTime getStartDateTime();

    LocalDateTime getEndDateTime();
}
