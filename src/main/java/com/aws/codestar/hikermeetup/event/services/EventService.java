package com.aws.codestar.hikermeetup.event.services;

import com.aws.codestar.hikermeetup.base.EntityNotFoundException;
import com.aws.codestar.hikermeetup.base.PatchModelMapper;
import com.aws.codestar.hikermeetup.event.data.Event;
import com.aws.codestar.hikermeetup.event.data.EventRepository;
import com.aws.codestar.hikermeetup.event.data.EventStatus;
import com.aws.codestar.hikermeetup.event.data.PagingEventRepository;
import com.aws.codestar.hikermeetup.event.exceptions.EventStatusException;
import com.aws.codestar.hikermeetup.event.exceptions.NotEventOrganizerException;
import com.aws.codestar.hikermeetup.event.web.EventInput;
import com.aws.codestar.hikermeetup.member.data.Member;
import com.aws.codestar.hikermeetup.member.services.MemberService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EventService {

    private final MemberService memberService;

    private final EventRepository eventRepository;

    private final PagingEventRepository pagingEventRepository;

    private final PatchModelMapper mapper;

    public EventService(MemberService memberService, EventRepository eventRepository, PagingEventRepository pagingEventRepository, PatchModelMapper mapper) {
        this.memberService = memberService;
        this.eventRepository = eventRepository;
        this.pagingEventRepository = pagingEventRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<Event> getEvents(Pageable pageable) {
        return pagingEventRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find event " + eventId));
    }

    public Event createEvent(EventInput eventInput) {
        Member organizer = memberService.getCurrentMember();

        Event event = new Event(organizer);
        mapper.map(eventInput, event);
        setEventStatusByAttendees(event);

        return eventRepository.save(event);
    }

    public Event updateEvent(UUID eventId, EventInput eventInput) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.PENDING, EventStatus.GREENLIT);
        ensureUserIsOrganizer(event.getOrganizer());

        mapper.map(eventInput, event);

        setEventStatusByAttendees(event);

        return eventRepository.save(event);
    }

    public Event startEvent(UUID eventId) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.PENDING, EventStatus.GREENLIT);
        ensureUserIsOrganizer(event.getOrganizer());

        event.setEventStatus(EventStatus.STARTED);

        return eventRepository.save(event);
    }

    public Event finishEvent(UUID eventId) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.STARTED);
        ensureUserIsOrganizer(event.getOrganizer());

        event.setEventStatus(EventStatus.FINISHED);

        return eventRepository.save(event);
    }

    public Event cancelEvent(UUID eventId) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.PENDING, EventStatus.GREENLIT, EventStatus.STARTED);
        ensureUserIsOrganizer(event.getOrganizer());

        event.setEventStatus(EventStatus.CANCELED);

        return eventRepository.save(event);
    }

    public Event addFollower(UUID eventId) {
        Event event = getEvent(eventId);

        Member member = memberService.getCurrentMember();

        List<Member> followers = event.getFollowers();
        if (followers == null) {
            followers = new ArrayList<>();
            event.setFollowers(followers);
        }

        if (!followers.contains(member))
            followers.add(member);

        return eventRepository.save(event);
    }

    public Event removeFollower(UUID eventId) {
        Event event = getEvent(eventId);

        Member member = memberService.getCurrentMember();

        List<Member> followers = event.getFollowers();
        if (followers != null) {
            followers.remove(member);
        }

        return eventRepository.save(event);
    }

    public Event addAttendee(UUID eventId) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.PENDING, EventStatus.GREENLIT);

        Member member = memberService.getCurrentMember();

        List<Member> attendees = event.getAttendees();
        if (attendees == null) {
            attendees = new ArrayList<>();
            event.setAttendees(attendees);
        }

        if (!attendees.contains(member))
            attendees.add(member);

        setEventStatusByAttendees(event);

        return eventRepository.save(event);
    }

    public Event removeAttendee(UUID eventId) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.PENDING, EventStatus.GREENLIT);

        Member member = memberService.getCurrentMember();

        List<Member> attendees = event.getAttendees();
        if (attendees != null) {
            attendees.remove(member);
        }

        setEventStatusByAttendees(event);

        return eventRepository.save(event);
    }

    private void setEventStatusByAttendees(Event event) {
        int noOfAttendees = event.getAttendees() == null ? 0 : event.getAttendees().size();
        int minAttendees = event.getMinAttendees() == null ? 0 : event.getMinAttendees();

        EventStatus eventStatus = noOfAttendees >= minAttendees ?
                EventStatus.GREENLIT :
                EventStatus.PENDING;

        event.setEventStatus(eventStatus);
    }

    private void ensureUserIsOrganizer(Member eventOrganizer) {
        if (!eventOrganizer.equals(memberService.getCurrentMember())) {
            throw new NotEventOrganizerException();
        }
    }

    private void ensureValidEventStatus(Event event, EventStatus... validEventStatuses) {
        EventStatus eventStatus = event.getEventStatus();

        if (!Arrays.asList(validEventStatuses).contains(eventStatus)) {
            String conjunction = "";
            switch(eventStatus) {
                case GREENLIT:
                case CANCELED:
                    conjunction = "had been";
                    break;
                case STARTED:
                case FINISHED:
                    conjunction = "had";
                    break;
                default:
                    conjunction = "is still";
            }

            throw new EventStatusException(
                    String.format("Unable to perform requested operation as the event %s %s.", conjunction, eventStatus.toString().toLowerCase()));
        }
    }
}
