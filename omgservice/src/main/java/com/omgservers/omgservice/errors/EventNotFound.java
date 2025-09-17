package com.omgservers.omgservice.errors;

import com.omgservers.omgservice.event.EventQualifier;

public class EventNotFound extends ResourceNotFound {

    public EventNotFound(final Long eventId) {
        super("Event %d not found".formatted(eventId));
    }

    public EventNotFound(final EventQualifier qualifier, final Long resourceId) {
        super("Event %s not found for resource %d".formatted(qualifier, resourceId));
    }
}
