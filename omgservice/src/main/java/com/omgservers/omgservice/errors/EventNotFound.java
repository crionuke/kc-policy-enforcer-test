package com.omgservers.omgservice.errors;

import com.omgservers.omgservice.event.EventQualifier;

public class EventNotFound extends ResourceNotFound {

    public EventNotFound(final EventQualifier qualifier, final Long resourceId) {
        super("Event %s not found for resource %d"
                .formatted(qualifier, resourceId));
    }
}
