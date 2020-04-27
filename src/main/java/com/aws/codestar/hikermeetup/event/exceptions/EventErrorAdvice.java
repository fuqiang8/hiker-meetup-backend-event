package com.aws.codestar.hikermeetup.event.exceptions;

import com.aws.codestar.hikermeetup.base.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class EventErrorAdvice {
    @ExceptionHandler(EventStatusException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorMessage handleEventStatusException(EventStatusException e) {
        return new ErrorMessage(e.getMessage());
    }

    @ExceptionHandler(NotEventOrganizerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage handleNotEventOrganizerException(NotEventOrganizerException e) {
        return new ErrorMessage(e.getMessage());
    }
}
