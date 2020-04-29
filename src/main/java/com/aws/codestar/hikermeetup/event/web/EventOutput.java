package com.aws.codestar.hikermeetup.event.web;

import com.aws.codestar.hikermeetup.event.data.EventStatus;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.List;

public interface EventOutput {
    @Value("#{target.getOrganizer() == null ? '' : target.getOrganizer().getName() ?: ''}")
    String getOrganizer();

    @Value("#{target.getAttendees() == null ? T(java.util.Collections).EMPTY_LIST : target.getAttendees().![name]}")
    List<String> getAttendees();

    @Value("#{target.getFollowers() == null ? T(java.util.Collections).EMPTY_LIST : target.getFollowers().![name]}")
    List<String> getFollowers();

    EventStatus getEventStatus();

    Integer getMinAttendees();

    @Value("#{target.getCategory() ?: ''}")
    String getCategory();

    String getName();

    String getLocation();

    LocalDateTime getStartDateTime();

    LocalDateTime getEndDateTime();
}
