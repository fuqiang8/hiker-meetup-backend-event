package com.aws.codestar.hikermeetup.event.services;

import com.aws.codestar.hikermeetup.base.PatchModelMapper;
import com.aws.codestar.hikermeetup.event.data.Event;
import com.aws.codestar.hikermeetup.event.data.EventRepository;
import com.aws.codestar.hikermeetup.event.data.EventStatus;
import com.aws.codestar.hikermeetup.event.exceptions.EventStatusException;
import com.aws.codestar.hikermeetup.event.web.EventInput;
import com.aws.codestar.hikermeetup.member.data.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class EventService {

    private final EventRepository eventRepository;

    private final PatchModelMapper mapper;

    public EventService(EventRepository eventRepository, PatchModelMapper mapper) {
        this.eventRepository = eventRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Page<Event> getEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Event getEvent(UUID eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find event " + eventId));
    }

    public Event createEvent(EventInput eventInput) {
        Event event = new Event();
        mapper.map(eventInput, event);
        event.setEventStatus(EventStatus.PENDING);

        return eventRepository.save(event);
    }

    public Event updateEvent(UUID eventId, EventInput eventInput) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.PENDING, EventStatus.GREENLIT);

        mapper.map(eventInput, event);

        EventStatus eventStatus = event.getAttendees().size() >= event.getMinAttendees() ?
                EventStatus.GREENLIT :
                EventStatus.PENDING;

        event.setEventStatus(eventStatus);

        return eventRepository.save(event);
    }

    public Event startEvent(UUID eventId) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.PENDING, EventStatus.GREENLIT);

        event.setEventStatus(EventStatus.CANCELLED);

        return eventRepository.save(event);
    }

    public Event cancelEvent(UUID eventId) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.PENDING, EventStatus.GREENLIT, EventStatus.STARTED);

        event.setEventStatus(EventStatus.CANCELLED);

        return eventRepository.save(event);
    }

    public Event addFollower(UUID eventId, Member member) {
        Event event = getEvent(eventId);
        EventStatus eventStatus = event.getEventStatus();

        if (eventStatus != EventStatus.PENDING && eventStatus != EventStatus.GREENLIT) {
            throw new EventStatusException(
                    String.format("Unable to perform requested operation as the event %s",
                            eventStatus == EventStatus.CANCELLED ?
                                    "had been " + eventStatus.toString().toLowerCase() :
                                    "had " + eventStatus.toString().toLowerCase()));
        }

        event.getFollowers().add(member);

        return eventRepository.save(event);
    }

    public Event removeFollower(UUID eventId, Member member) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.PENDING, EventStatus.GREENLIT);

        event.getFollowers().remove(member);

        return eventRepository.save(event);
    }

    public Event addAttendee(UUID eventId, Member member) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.PENDING, EventStatus.GREENLIT);

        event.getAttendees().add(member);

        EventStatus eventStatus = event.getAttendees().size() >= event.getMinAttendees() ?
                EventStatus.GREENLIT :
                EventStatus.PENDING;

        event.setEventStatus(eventStatus);

        return eventRepository.save(event);
    }

    public Event removeAttendee(UUID eventId, Member member) {
        Event event = getEvent(eventId);
        ensureValidEventStatus(event, EventStatus.PENDING, EventStatus.GREENLIT);

        event.getAttendees().remove(member);

        EventStatus eventStatus = event.getAttendees().size() >= event.getMinAttendees() ?
                EventStatus.GREENLIT :
                EventStatus.PENDING;

        event.setEventStatus(eventStatus);

        return eventRepository.save(event);
    }

//    private void ensurePendingOrGreenlit(Event event) {
//        EventStatus eventStatus = event.getEventStatus();
//
//        if (eventStatus != EventStatus.PENDING && eventStatus != EventStatus.GREENLIT) {
//            throw new EventStatusException(
//                    String.format("Unable to perform requested operation as the event %s",
//                            eventStatus == EventStatus.CANCELLED ?
//                                    "had been " + eventStatus.toString().toLowerCase() :
//                                    "had " + eventStatus.toString().toLowerCase()));
//        }
//    }

    private void ensureValidEventStatus(Event event, EventStatus... validEventStatuses) {
        EventStatus eventStatus = event.getEventStatus();

        if (!List.of(validEventStatuses).contains(eventStatus)) {
            String conjunction = "";
            switch(eventStatus) {
                case GREENLIT:
                case CANCELLED:
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
