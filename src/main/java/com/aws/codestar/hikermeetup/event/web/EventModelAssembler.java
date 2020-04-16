package com.aws.codestar.hikermeetup.event.web;

import com.aws.codestar.hikermeetup.event.data.Event;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EventModelAssembler implements RepresentationModelAssembler<Event, EntityModel<Event>> {

    @Override
    public EntityModel<Event> toModel(Event event) {
        return new EntityModel<>(event,
                linkTo(methodOn(EventController.class).getEvent(event.getId()))
                        .withSelfRel(),
                linkTo(methodOn(EventController.class).getEvents(PageRequest.of(0, 20)))
                        .withRel("events"));
    }
}
