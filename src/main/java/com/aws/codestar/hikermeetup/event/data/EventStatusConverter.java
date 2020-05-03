package com.aws.codestar.hikermeetup.event.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;

public class EventStatusConverter implements DynamoDBTypeConverter<String, EventStatus> {

    @Override
    public String convert(EventStatus eventStatus) {
        return eventStatus.name();
    }

    @Override
    public EventStatus unconvert(String s) {
        return EventStatus.valueOf(s);
    }
}
