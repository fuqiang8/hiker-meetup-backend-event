package com.aws.codestar.hikermeetup.event.web;

import com.aws.codestar.hikermeetup.event.data.Event;
import com.aws.codestar.hikermeetup.event.services.EventService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final EventModelAssembler eventModelAssembler;
    private final PagedResourcesAssembler<Event> pagedResourcesAssembler;

    /*
    * TODO: ALL EVENTS REQUIRE USER INFO.
    *  updateEvent, startEvent, cancelEvent should be organizer only.
    * */
    public EventController(EventService eventService,
                           EventModelAssembler eventModelAssembler,
                           PagedResourcesAssembler<Event> pagedResourcesAssembler) {
        this.eventService = eventService;
        this.eventModelAssembler = eventModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    public PagedModel<EntityModel<EventOutput>> getEvents(Pageable pageable) {
        Page<Event> eventPage = eventService.getEvents(pageable);
        return pagedResourcesAssembler.toModel(eventPage, eventModelAssembler);
    }

    @GetMapping("/{eventId}")
    public EntityModel<EventOutput> getEvent(@PathVariable UUID eventId) {
        Event event = eventService.getEvent(eventId);
        return eventModelAssembler.toModel(event);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<EventOutput>> createEvent(@Valid @RequestBody EventInput eventInput) {
        Event event = eventService.createEvent(eventInput);
        EntityModel<EventOutput> entityModel = eventModelAssembler.toModel(event);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PatchMapping("/{eventId}")
    public EntityModel<EventOutput> updateEvent(@PathVariable UUID eventId,
                                          @RequestBody EventInput eventInput) {
        Event event = eventService.updateEvent(eventId, eventInput);
        return eventModelAssembler.toModel(event);
    }

    @PostMapping("/{eventId}/start")
    public EntityModel<?> startEvent(@PathVariable UUID eventId) {
        Event event = eventService.startEvent(eventId);
        return eventModelAssembler.toModel(event);
    }

    @PostMapping("/{eventId}/cancel")
    public EntityModel<?> cancelEvent(@PathVariable UUID eventId) {
        Event event = eventService.cancelEvent(eventId);
        return eventModelAssembler.toModel(event);
    }

    @PostMapping("/{eventId}/like")
    public EntityModel<EventOutput> likeEvent(@PathVariable UUID eventId) {
        Event event = eventService.addFollower(eventId);
        return eventModelAssembler.toModel(event);
    }

    @PostMapping("/{eventId}/unlike")
    public EntityModel<EventOutput> unlikeEvent(@PathVariable UUID eventId) {
        Event event = eventService.removeFollower(eventId);
        return eventModelAssembler.toModel(event);
    }

    @PostMapping("/{eventId}/attend")
    public EntityModel<EventOutput> attendEvent(@PathVariable UUID eventId) {
        Event event = eventService.addAttendee(eventId);
        return eventModelAssembler.toModel(event);
    }

    @PostMapping("/{eventId}/miss")
    public EntityModel<EventOutput> missEvent(@PathVariable UUID eventId) {
        Event event = eventService.removeAttendee(eventId);
        return eventModelAssembler.toModel(event);
    }
}
