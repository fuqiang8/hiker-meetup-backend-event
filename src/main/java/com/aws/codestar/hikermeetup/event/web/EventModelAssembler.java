package com.aws.codestar.hikermeetup.event.web;

import com.aws.codestar.hikermeetup.event.data.Event;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EventModelAssembler implements RepresentationModelAssembler<Event, EntityModel<EventOutput>> {

    private final ProjectionFactory projectionFactory;

    public EventModelAssembler(ProjectionFactory projectionFactory) {
        this.projectionFactory = projectionFactory;
    }

    @Override
    public EntityModel<EventOutput> toModel(Event event) {
        EventOutput eventOutput = projectionFactory.createProjection(EventOutput.class, event);

        return new EntityModel<>(eventOutput,
                linkTo(methodOn(EventController.class).getEvent(event.getId()))
                        .withSelfRel(),
                linkTo(methodOn(EventController.class).getEvents(PageRequest.of(0, 20)))
                        .withRel("events"),
                linkTo(methodOn(EventController.class).likeEvent(event.getId()))
                        .withRel("like"),
                linkTo(methodOn(EventController.class).unlikeEvent(event.getId()))
                        .withRel("unlike"),
                linkTo(methodOn(EventController.class).attendEvent(event.getId()))
                        .withRel("attend"),
                linkTo(methodOn(EventController.class).missEvent(event.getId()))
                        .withRel("miss"));
    }
}
