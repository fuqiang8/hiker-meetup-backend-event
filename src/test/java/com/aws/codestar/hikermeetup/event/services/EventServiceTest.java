package com.aws.codestar.hikermeetup.event.services;

import com.aws.codestar.hikermeetup.base.PatchModelMapper;
import com.aws.codestar.hikermeetup.event.data.Event;
import com.aws.codestar.hikermeetup.event.data.EventRepository;
import com.aws.codestar.hikermeetup.event.data.EventStatus;
import com.aws.codestar.hikermeetup.event.web.EventInput;
import com.aws.codestar.hikermeetup.member.services.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class EventServiceTest {
    @InjectMocks
    EventService eventService;

    @Mock
    MemberService memberService;

    @Mock
    EventRepository eventRepository;

    @Mock
    PatchModelMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(eventRepository.save(any(Event.class))).thenAnswer(i -> {
            UUID id = (UUID) ReflectionTestUtils.getField((Event) i.getArgument(0), "id");

            if (id == null) {
                ReflectionTestUtils.setField((Event) i.getArgument(0), "id", UUID.randomUUID());
            }

            return i.getArgument(0);
        });
    }

    @Test
    void createEvent() {
        Event event = new Event();
        event.setEventStatus(EventStatus.PENDING);
        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.createEvent(new EventInput());
        assertEquals(EventStatus.PENDING, result.getEventStatus());
    }

    @Test
    void updateEvent() {
    }

    @Test
    void startEvent() {
    }

    @Test
    void cancelEvent() {
    }

    @Test
    void addFollower() {
    }

    @Test
    void removeFollower() {
    }

    @Test
    void addAttendee() {
    }

    @Test
    void removeAttendee() {
    }
}