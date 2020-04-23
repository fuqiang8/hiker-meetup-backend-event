package com.aws.codestar.hikermeetup.event.services;

import com.aws.codestar.hikermeetup.base.PatchModelMapper;
import com.aws.codestar.hikermeetup.event.data.Event;
import com.aws.codestar.hikermeetup.event.data.EventRepository;
import com.aws.codestar.hikermeetup.event.data.EventStatus;
import com.aws.codestar.hikermeetup.event.exceptions.EventStatusException;
import com.aws.codestar.hikermeetup.event.web.EventInput;
import com.aws.codestar.hikermeetup.member.data.Member;
import com.aws.codestar.hikermeetup.member.services.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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
    void updateEvent_DecreaseMinAttendeeToGreenLit() {
        Member organizer = generateMember("organizer");
        when(memberService.getOrCreateCurrentMember()).thenReturn(organizer);
        setupSuccessfulEventRetrieval(organizer, EventStatus.PENDING, 5);
        setupMinAttendeeMapper();

        Integer newMin = Integer.valueOf(2);
        EventInput eventInput = new EventInput();
        eventInput.setMinAttendees(newMin);

        Event result = eventService.updateEvent(UUID.randomUUID(), eventInput);
        assertEquals(EventStatus.GREENLIT, result.getEventStatus());
        assertEquals(newMin, result.getMinAttendees());
    }

    @Test()
    void updateEvent_IncreaseMinAttendeeToPending() {
        Member organizer = generateMember("organizer");
        when(memberService.getOrCreateCurrentMember()).thenReturn(organizer);
        setupSuccessfulEventRetrieval(organizer, EventStatus.GREENLIT, 5);
        setupMinAttendeeMapper();

        Integer newMin = Integer.valueOf(7);
        EventInput eventInput = new EventInput();
        eventInput.setMinAttendees(newMin);

        Event result = eventService.updateEvent(UUID.randomUUID(), eventInput);
        assertEquals(EventStatus.PENDING, result.getEventStatus());
        assertEquals(newMin, result.getMinAttendees());
    }

    @Test
    void updateEvent_MatchMinAttendeeToGreenLit() {
        Member organizer = generateMember("organizer");
        when(memberService.getOrCreateCurrentMember()).thenReturn(organizer);
        setupSuccessfulEventRetrieval(organizer, EventStatus.PENDING, 5);
        setupMinAttendeeMapper();

        Integer newMin = Integer.valueOf(5);
        EventInput eventInput = new EventInput();
        eventInput.setMinAttendees(newMin);

        Event result = eventService.updateEvent(UUID.randomUUID(), eventInput);
        assertEquals(EventStatus.GREENLIT, result.getEventStatus());
        assertEquals(newMin, result.getMinAttendees());
    }

    @Test
    void updateEvent_EventStarted() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.STARTED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.updateEvent(UUID.randomUUID(), new EventInput());
        });

        assertTrue(exception.getMessage().contains("had started"));
    }

    @Test
    void updateEvent_EventFinished() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.FINISHED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.updateEvent(UUID.randomUUID(), new EventInput());
        });

        assertTrue(exception.getMessage().contains("had finished"));
    }

    @Test
    void updateEvent_EventCanceled() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.CANCELED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.updateEvent(UUID.randomUUID(), new EventInput());
        });

        assertTrue(exception.getMessage().contains("had been canceled"));
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

    private Member generateMember(String name) {
        Member member = new Member(UUID.randomUUID(), name);
        ReflectionTestUtils.setField(member, "id", UUID.randomUUID());
        return member;
    }

    private void setupMinAttendeeMapper() {
        doAnswer(i -> {
            Integer min = (Integer) ReflectionTestUtils.getField((EventInput) i.getArgument(0), "minAttendees");

            ReflectionTestUtils.setField((Event) i.getArgument(1), "minAttendees", min);
            return i.getArgument(1);
        }).when(mapper).map(any(EventInput.class), any(Event.class));
    }

    private void setupSuccessfulEventRetrieval(Member organizer, EventStatus currentEventStatus) {
        setupSuccessfulEventRetrieval(organizer, currentEventStatus, 0);
    }

    private void setupSuccessfulEventRetrieval(Member organizer, EventStatus currentEventStatus, int currentAttendees) {
        Set<Member> attendees = new HashSet<>();
        for (int i = 0; i < currentAttendees; i++) {
            attendees.add(generateMember("attendee" + i));
        }

        Event event = new Event(organizer);
        event.setAttendees(attendees);
        event.setEventStatus(currentEventStatus);

        when(eventRepository.findById(any(UUID.class))).thenAnswer(i -> {
            ReflectionTestUtils.setField(event, "id", i.getArgument(0));
            return Optional.of(event);
        });
    }
}