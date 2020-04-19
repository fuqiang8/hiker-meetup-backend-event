package com.aws.codestar.hikermeetup.event.exceptions;

public class NotEventOrganizerException extends RuntimeException {
    public NotEventOrganizerException() {
        super("The operation has failed due to insufficient permission. Logged in user is not the event's organizer.");
    }
}
