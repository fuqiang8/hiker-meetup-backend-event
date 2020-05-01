package com.aws.codestar.hikermeetup.event.web;

import com.aws.codestar.hikermeetup.base.ErrorMessage;
import com.aws.codestar.hikermeetup.event.data.Event;
import com.aws.codestar.hikermeetup.event.services.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@SecurityScheme(name = "auth",
        type = SecuritySchemeType.OAUTH2,
        flows = @OAuthFlows( implicit = @OAuthFlow(
                authorizationUrl = "https://hiker-meetup.auth.ap-southeast-1.amazoncognito.com/oauth2/authorize",
                scopes = { @OAuthScope(name = "openid", description = "") } )))
@Tag(name = "Events", description = "Events API")
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

    @Operation(summary = "Get events in a pageable format",
            description = "Retrieves all events. Defaults to 20 records per page unless specified otherwise in the request. ")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    @GetMapping
    public PagedModel<EntityModel<EventOutput>> getEvents(Pageable pageable) {
        Page<Event> eventPage = eventService.getEvents(pageable);
        return pagedResourcesAssembler.toModel(eventPage, eventModelAssembler);
    }

    @Operation(summary = "Get event by eventId",
            description = "Get the event by eventId. Respond with 404 if not found.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Event not found",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class)))) })
    @GetMapping("/{eventId}")
    public EntityModel<EventOutput> getEvent(@PathVariable UUID eventId) {
        Event event = eventService.getEvent(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Create event",
            description = "Create event with logged in user as the organizer. " +
                    "Respond with 403 if there is no logged in user.",
            security = { @SecurityRequirement(name = "auth", scopes = { "openid" }) })
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Successful operation"),
            @ApiResponse(responseCode = "403", description = "Require user login",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class)))) })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<EventOutput>> createEvent(@Valid @RequestBody EventInput eventInput) {
        Event event = eventService.createEvent(eventInput);
        EntityModel<EventOutput> entityModel = eventModelAssembler.toModel(event);

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @Operation(summary = "Update existing event",
            description = "Respond with 403 if there is no logged in user / logged in user is not the event's organizer.",
            security = { @SecurityRequirement(name = "auth", scopes = { "openid" }) })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "403", description = "Require user login / User is not the event's organizer",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class)))) })
    @PatchMapping("/{eventId}")
    public EntityModel<EventOutput> updateEvent(@PathVariable UUID eventId,
                                          @RequestBody EventInput eventInput) {
        Event event = eventService.updateEvent(eventId, eventInput);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Start existing event",
            description = "Respond with 403 if there is no logged in user / logged in user is not the event's organizer. " +
                    "Respond with 422 if event had started, finished or had been cancelled.",
            security = { @SecurityRequirement(name = "auth", scopes = { "openid" }) })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "403", description = "Require user login / User is not the event's organizer",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class)))),
            @ApiResponse(responseCode = "422", description = "Event had started, finished or had been cancelled.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class))))})
    @PostMapping("/{eventId}/start")
    public EntityModel<?> startEvent(@PathVariable UUID eventId) {
        Event event = eventService.startEvent(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Cancel existing event",
            description = "Respond with 403 if there is no logged in user / logged in user is not the event's organizer. " +
                    "Respond with 422 if event had finished or had been cancelled.",
            security = { @SecurityRequirement(name = "auth", scopes = { "openid" }) })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "403", description = "Require user login / User is not the event's organizer",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class)))),
            @ApiResponse(responseCode = "422", description = "Event had finished or had been cancelled.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class))))})
    @PostMapping("/{eventId}/cancel")
    public EntityModel<?> cancelEvent(@PathVariable UUID eventId) {
        Event event = eventService.cancelEvent(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Follow / Like existing event",
            description = "Add user as one of the event's followers. " +
                    "Respond with 403 if there is no logged in user. " +
                    "Respond with 422 if event had started, finished or had been cancelled.",
            security = { @SecurityRequirement(name = "auth", scopes = { "openid" }) })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "403", description = "Require user login",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class)))),
            @ApiResponse(responseCode = "422", description = "Event had started, finished or had been cancelled.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class))))})
    @PostMapping("/{eventId}/like")
    public EntityModel<EventOutput> likeEvent(@PathVariable UUID eventId) {
        Event event = eventService.addFollower(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Stop following / Unlike existing event",
            description = "Remove user as one of the event's followers. " +
                    "Respond with 403 if there is no logged in user. " +
                    "Respond with 422 if event had started, finished or had been cancelled.",
            security = { @SecurityRequirement(name = "auth", scopes = { "openid" }) })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "403", description = "Require user login",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class)))),
            @ApiResponse(responseCode = "422", description = "Event had started, finished or had been cancelled.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class))))})
    @PostMapping("/{eventId}/unlike")
    public EntityModel<EventOutput> unlikeEvent(@PathVariable UUID eventId) {
        Event event = eventService.removeFollower(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Attend existing event",
            description = "Add user as one of the event's attendees. " +
                    "Respond with 403 if there is no logged in user. " +
                    "Respond with 422 if event had started, finished or had been cancelled.",
            security = { @SecurityRequirement(name = "auth", scopes = { "openid" }) })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "403", description = "Require user login",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class)))),
            @ApiResponse(responseCode = "422", description = "Event had started, finished or had been cancelled.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class))))})
    @PostMapping("/{eventId}/attend")
    public EntityModel<EventOutput> attendEvent(@PathVariable UUID eventId) {
        Event event = eventService.addAttendee(eventId);
        return eventModelAssembler.toModel(event);
    }

    @Operation(summary = "Miss existing event",
            description = "Remove user as one of the event's attendees. " +
                    "Respond with 403 if there is no logged in user. " +
                    "Respond with 422 if event had started, finished or had been cancelled.",
            security = { @SecurityRequirement(name = "auth", scopes = { "openid" }) })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "403", description = "Require user login",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class)))),
            @ApiResponse(responseCode = "422", description = "Event had started, finished or had been cancelled.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorMessage.class))))})
    @PostMapping("/{eventId}/miss")
    public EntityModel<EventOutput> missEvent(@PathVariable UUID eventId) {
        Event event = eventService.removeAttendee(eventId);
        return eventModelAssembler.toModel(event);
    }
}
