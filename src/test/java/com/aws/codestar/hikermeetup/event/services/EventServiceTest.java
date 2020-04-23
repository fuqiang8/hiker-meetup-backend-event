package com.aws.codestar.hikermeetup.event.services;

import com.aws.codestar.hikermeetup.base.PatchModelMapper;
import com.aws.codestar.hikermeetup.event.data.Event;
import com.aws.codestar.hikermeetup.event.data.EventRepository;
import com.aws.codestar.hikermeetup.event.data.EventStatus;
import com.aws.codestar.hikermeetup.event.exceptions.EventStatusException;
import com.aws.codestar.hikermeetup.event.exceptions.NotEventOrganizerException;
import com.aws.codestar.hikermeetup.event.web.EventInput;
import com.aws.codestar.hikermeetup.member.data.Member;
import com.aws.codestar.hikermeetup.member.services.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

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
        Event result = eventService.createEvent(new EventInput());
        assertEquals(EventStatus.PENDING, result.getEventStatus());
    }

    @Test
    void updateEvent_DecreaseMinAttendeeToGreenLit() {
        Member organizer = generateMember("organizer");
        when(memberService.getOrCreateCurrentMember()).thenReturn(organizer);
        setupSuccessfulEventRetrieval(organizer, EventStatus.PENDING, 0, 5);
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
        setupSuccessfulEventRetrieval(organizer, EventStatus.GREENLIT, 0, 5);
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
        setupSuccessfulEventRetrieval(organizer, EventStatus.PENDING, 0, 5);
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
    void updateEvent_NotOrganizer() {
        when(memberService.getOrCreateCurrentMember()).thenReturn(generateMember("current"));
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.PENDING);

        Exception exception = assertThrows(NotEventOrganizerException.class, () -> {
            eventService.updateEvent(UUID.randomUUID(), new EventInput());
        });

        assertTrue(exception.getMessage().contains("insufficient permission"));
    }

    @Test
    void startEvent_EventPending() {
        Member organizer = generateMember("organizer");
        when(memberService.getOrCreateCurrentMember()).thenReturn(organizer);
        setupSuccessfulEventRetrieval(organizer, EventStatus.PENDING);

        Event result = eventService.startEvent(UUID.randomUUID());
        assertEquals(EventStatus.STARTED, result.getEventStatus());
    }

    @Test
    void startEvent_EventGreenLit() {
        Member organizer = generateMember("organizer");
        when(memberService.getOrCreateCurrentMember()).thenReturn(organizer);
        setupSuccessfulEventRetrieval(organizer, EventStatus.GREENLIT);

        Event result = eventService.startEvent(UUID.randomUUID());
        assertEquals(EventStatus.STARTED, result.getEventStatus());
    }

    @Test
    void startEvent_EventStarted() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.STARTED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.startEvent(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had started"));
    }

    @Test
    void startEvent_EventFinished() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.FINISHED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.startEvent(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had finished"));
    }

    @Test
    void startEvent_EventCanceled() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.CANCELED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.startEvent(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had been canceled"));
    }

    @Test
    void startEvent_NotOrganizer() {
        when(memberService.getOrCreateCurrentMember()).thenReturn(generateMember("current"));
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.PENDING);

        Exception exception = assertThrows(NotEventOrganizerException.class, () -> {
            eventService.startEvent(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("insufficient permission"));
    }

    @Test
    void cancelEvent_EventPending() {
        Member organizer = generateMember("organizer");
        when(memberService.getOrCreateCurrentMember()).thenReturn(organizer);
        setupSuccessfulEventRetrieval(organizer, EventStatus.PENDING);

        Event result = eventService.cancelEvent(UUID.randomUUID());
        assertEquals(EventStatus.CANCELED, result.getEventStatus());
    }

    @Test
    void cancelEvent_EventGreenLit() {
        Member organizer = generateMember("organizer");
        when(memberService.getOrCreateCurrentMember()).thenReturn(organizer);
        setupSuccessfulEventRetrieval(organizer, EventStatus.GREENLIT);

        Event result = eventService.cancelEvent(UUID.randomUUID());
        assertEquals(EventStatus.CANCELED, result.getEventStatus());
    }

    @Test
    void cancelEvent_EventStarted() {
        Member organizer = generateMember("organizer");
        when(memberService.getOrCreateCurrentMember()).thenReturn(organizer);
        setupSuccessfulEventRetrieval(organizer, EventStatus.STARTED);

        Event result = eventService.cancelEvent(UUID.randomUUID());
        assertEquals(EventStatus.CANCELED, result.getEventStatus());
    }

    @Test
    void cancelEvent_EventFinished() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.FINISHED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.cancelEvent(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had finished"));
    }

    @Test
    void cancelEvent_EventCanceled() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.CANCELED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.cancelEvent(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had been canceled"));
    }

    @Test
    void cancelEvent_NotOrganizer() {
        when(memberService.getOrCreateCurrentMember()).thenReturn(generateMember("current"));
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.PENDING);

        Exception exception = assertThrows(NotEventOrganizerException.class, () -> {
            eventService.cancelEvent(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("insufficient permission"));
    }

    @Test
    void addFollower_EventPending() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.PENDING, 2,0);

        Event result = eventService.addFollower(UUID.randomUUID());
        assertEquals(3, result.getFollowers().size());
    }

    @Test
    void addFollower_EventGreenLit() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.GREENLIT, 2,0);

        Event result = eventService.addFollower(UUID.randomUUID());
        assertEquals(3, result.getFollowers().size());
    }

    @Test
    void addFollower_EventStarted() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.STARTED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.addFollower(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had started"));
    }

    @Test
    void addFollower_EventFinished() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.FINISHED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.addFollower(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had finished"));
    }

    @Test
    void addFollower_EventCanceled() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.CANCELED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.addFollower(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had been canceled"));
    }

    @Test
    void removeFollower_EventPending() {
        Member current = generateMember("current");
        HashSet followers = new HashSet();
        followers.add(current);
        followers.add(generateMember("follower1"));

        when(memberService.getOrCreateCurrentMember()).thenReturn(current);
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.PENDING, followers, Collections.EMPTY_SET);

        Event result = eventService.removeFollower(UUID.randomUUID());
        assertEquals(1, result.getFollowers().size());
        assertTrue(!result.getFollowers().contains(current));
    }

    @Test
    void removeFollower_EventGreenLit() {
        Member current = generateMember("current");
        HashSet followers = new HashSet();
        followers.add(current);
        followers.add(generateMember("follower1"));

        when(memberService.getOrCreateCurrentMember()).thenReturn(current);
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.GREENLIT, followers, Collections.EMPTY_SET);

        Event result = eventService.removeFollower(UUID.randomUUID());
        assertEquals(1, result.getFollowers().size());
        assertTrue(!result.getFollowers().contains(current));
    }

    @Test
    void removeFollower_EventStarted() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.STARTED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.removeFollower(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had started"));
    }

    @Test
    void removeFollower_EventFinished() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.FINISHED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.removeFollower(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had finished"));
    }

    @Test
    void removeFollower_EventCanceled() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.CANCELED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.removeFollower(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had been canceled"));
    }

    @Test
    void addAttendee_PendingToGreenLit() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.PENDING, 0,2, 3);

        Event result = eventService.addAttendee(UUID.randomUUID());
        assertEquals(3, result.getAttendees().size());
        assertEquals(EventStatus.GREENLIT, result.getEventStatus());
    }

    @Test
    void addAttendee_EventPending() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.PENDING, 0,2, 4);

        Event result = eventService.addAttendee(UUID.randomUUID());
        assertEquals(3, result.getAttendees().size());
        assertEquals(EventStatus.PENDING, result.getEventStatus());
    }

    @Test
    void addAttendee_EventGreenLit() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.GREENLIT, 0,2, 2);

        Event result = eventService.addAttendee(UUID.randomUUID());
        assertEquals(3, result.getAttendees().size());
    }

    @Test
    void addAttendee_EventStarted() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.STARTED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.addAttendee(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had started"));
    }

    @Test
    void addAttendee_EventFinished() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.FINISHED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.addAttendee(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had finished"));
    }

    @Test
    void addAttendee_EventCanceled() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.CANCELED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.addAttendee(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had been canceled"));
    }

    @Test
    void removeAttendee_GreenLitToPending() {
        Member current = generateMember("current");
        HashSet attendees = new HashSet();
        attendees.add(current);
        attendees.add(generateMember("attendee1"));
        attendees.add(generateMember("attendee2"));

        when(memberService.getOrCreateCurrentMember()).thenReturn(current);
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.GREENLIT, Collections.EMPTY_SET, attendees, 3);

        Event result = eventService.removeAttendee(UUID.randomUUID());
        assertEquals(2, result.getAttendees().size());
        assertTrue(!result.getAttendees().contains(current));
        assertEquals(EventStatus.PENDING, result.getEventStatus());
    }

    @Test
    void removeAttendee_EventPending() {
        Member current = generateMember("current");
        HashSet attendees = new HashSet();
        attendees.add(current);
        attendees.add(generateMember("attendee1"));

        when(memberService.getOrCreateCurrentMember()).thenReturn(current);
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.PENDING, Collections.EMPTY_SET, attendees, 3);

        Event result = eventService.removeAttendee(UUID.randomUUID());
        assertEquals(1, result.getAttendees().size());
        assertTrue(!result.getAttendees().contains(current));
        assertEquals(EventStatus.PENDING, result.getEventStatus());
    }

    @Test
    void removeAttendee_EventGreenLit() {
        Member current = generateMember("current");
        HashSet attendees = new HashSet();
        attendees.add(current);
        attendees.add(generateMember("attendee1"));
        attendees.add(generateMember("attendee2"));
        attendees.add(generateMember("attendee3"));

        when(memberService.getOrCreateCurrentMember()).thenReturn(current);
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.GREENLIT, Collections.EMPTY_SET, attendees, 3);

        Event result = eventService.removeAttendee(UUID.randomUUID());
        assertEquals(3, result.getAttendees().size());
        assertTrue(!result.getAttendees().contains(current));
        assertEquals(EventStatus.GREENLIT, result.getEventStatus());
    }

    @Test
    void removeAttendee_EventStarted() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.STARTED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.removeAttendee(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had started"));
    }

    @Test
    void removeAttendee_EventFinished() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.FINISHED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.removeAttendee(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had finished"));
    }

    @Test
    void removeAttendee_EventCanceled() {
        setupSuccessfulEventRetrieval(generateMember("organizer"), EventStatus.CANCELED);

        Exception exception = assertThrows(EventStatusException.class, () -> {
            eventService.removeAttendee(UUID.randomUUID());
        });

        assertTrue(exception.getMessage().contains("had been canceled"));
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
        setupSuccessfulEventRetrieval(organizer, currentEventStatus, 0, 0);
    }

    private void setupSuccessfulEventRetrieval(Member organizer, EventStatus currentEventStatus, int followersCount, int attendeesCount) {
        setupSuccessfulEventRetrieval(organizer, currentEventStatus, followersCount, attendeesCount, 0);
    }

    private void setupSuccessfulEventRetrieval(Member organizer, EventStatus currentEventStatus, Set<Member> followers, Set<Member> attendees) {
        setupSuccessfulEventRetrieval(organizer, currentEventStatus, followers, attendees, 0);
    }

    private void setupSuccessfulEventRetrieval(Member organizer, EventStatus currentEventStatus, int followersCount, int attendeesCount, int minAttendees) {
        Set<Member> followers = new HashSet<>();
        for (int i = 0; i < followersCount; i++) {
            followers.add(generateMember("follower" + i));
        }

        Set<Member> attendees = new HashSet<>();
        for (int i = 0; i < attendeesCount; i++) {
            attendees.add(generateMember("attendee" + i));
        }

        setupSuccessfulEventRetrieval(organizer, currentEventStatus, followers, attendees, minAttendees);
    }

    private void setupSuccessfulEventRetrieval(Member organizer, EventStatus currentEventStatus, Set<Member> followers, Set<Member> attendees, int minAttendees) {
        Event event = new Event(organizer);
        event.setFollowers(followers);
        event.setAttendees(attendees);
        event.setEventStatus(currentEventStatus);
        event.setMinAttendees(minAttendees);

        when(eventRepository.findById(any(UUID.class))).thenAnswer(i -> {
            ReflectionTestUtils.setField(event, "id", i.getArgument(0));
            return Optional.of(event);
        });
    }
}