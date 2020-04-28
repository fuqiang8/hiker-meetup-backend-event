package com.aws.codestar.hikermeetup.event.web;

import com.aws.codestar.hikermeetup.event.data.Event;
import com.aws.codestar.hikermeetup.event.services.EventService;
import com.aws.codestar.hikermeetup.member.services.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@Tag(name = "Events", description = "Events API")
public class EventController {
    private final EventService eventService;
    private final EventModelAssembler eventModelAssembler;
    private final PagedResourcesAssembler<Event> pagedResourcesAssembler;

    private final MemberService memberService;

    private static UUID uuid = UUID.fromString("ab8fba95-bb48-407b-8f74-e7f368e473d9");

    /*
    * TODO: ALL EVENTS REQUIRE USER INFO.
    *  updateEvent, startEvent, cancelEvent should be organizer only.
    * */
    public EventController(EventService eventService,
                           EventModelAssembler eventModelAssembler,
                           PagedResourcesAssembler<Event> pagedResourcesAssembler,
                           MemberService memberService) {
        this.eventService = eventService;
        this.eventModelAssembler = eventModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
        this.memberService = memberService;
    }

    @Operation(summary = "Get events in a pageable format",
            description = "Retrieves all events. Defaults to 20 records per page unless specified otherwise in the request. ")
    @GetMapping
    public PagedModel<EntityModel<Event>> getEvents(Pageable pageable) {
        Page<Event> eventPage = eventService.getEvents(pageable);
        return pagedResourcesAssembler.toModel(eventPage, eventModelAssembler);
    }

    @Operation(summary = "Get event by eventId",
            description = "Get the event by eventId. Respond with 404 if not found.")
    @GetMapping("/{eventId}")
    public EntityModel<Event> getEvent(@PathVariable UUID eventId) {
        Event event = eventService.getEvent(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Create event",
            description = "Create event with logged in user as the organizer. " +
                    "Respond with 403 if there is no logged in user.")
    @PostMapping
    public ResponseEntity<?> createEvent(@Valid @RequestBody EventInput eventInput) {
        Event event = eventService.createEvent(eventInput);
        EntityModel<Event> entityModel = eventModelAssembler.toModel(event);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @Operation(summary = "Update existing event",
            description = "Respond with 403 if there is no logged in user / logged in user is not the event's organizer.")
    @PatchMapping("/{eventId}")
    public EntityModel<Event> updateEvent(@PathVariable UUID eventId,
                                          @RequestBody EventInput eventInput) {
        Event event = eventService.updateEvent(eventId, eventInput);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Start existing event",
            description = "Respond with 403 if there is no logged in user / logged in user is not the event's organizer. " +
                    "Respond with 422 if event had started, finished or had been cancelled.")
    @PostMapping("/{eventId}/start")
    public EntityModel<Event> startEvent(@PathVariable UUID eventId) {
        Event event = eventService.startEvent(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Cancel existing event",
            description = "Respond with 403 if there is no logged in user / logged in user is not the event's organizer. " +
                    "Respond with 422 if event had finished or had been cancelled.")
    @PostMapping("/{eventId}/cancel")
    public EntityModel<Event> cancelEvent(@PathVariable UUID eventId) {
        Event event = eventService.cancelEvent(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Follow / Like existing event",
            description = "Add user as one of the event's followers. " +
                    "Respond with 403 if there is no logged in user. " +
                    "Respond with 422 if event had started, finished or had been cancelled.")
    @PostMapping("/{eventId}/like")
    public EntityModel<Event> likeEvent(@PathVariable UUID eventId) {
        Event event = eventService.addFollower(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Stop following / Unlike existing event",
            description = "Remove user as one of the event's followers. " +
                    "Respond with 403 if there is no logged in user. " +
                    "Respond with 422 if event had started, finished or had been cancelled.")
    @PostMapping("/{eventId}/unlike")
    public EntityModel<Event> unlikeEvent(@PathVariable UUID eventId) {
        Event event = eventService.removeFollower(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Attend existing event",
            description = "Add user as one of the event's attendees. " +
                    "Respond with 403 if there is no logged in user. " +
                    "Respond with 422 if event had started, finished or had been cancelled.")
    @PostMapping("/{eventId}/attend")
    public EntityModel<Event> attendEvent(@PathVariable UUID eventId) {
        Event event = eventService.addAttendee(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Miss existing event",
            description = "Remove user as one of the event's attendees. " +
                    "Respond with 403 if there is no logged in user. " +
                    "Respond with 422 if event had started, finished or had been cancelled.")
    @PostMapping("/{eventId}/miss")
    public EntityModel<Event> missEvent(@PathVariable UUID eventId) {
        Event event = eventService.removeAttendee(eventId);
        return eventModelAssembler.toModel(event);
    }
}
