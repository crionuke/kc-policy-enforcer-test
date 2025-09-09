package com.omgservers.tenants.errors;

import com.omgservers.tenants.event.EventQualifier;

import java.util.UUID;

public class EventNotFound extends ResourceNotFound {

    public EventNotFound(final EventQualifier qualifier, final UUID resourceId) {
        super("Event %s not found for resource %s"
                .formatted(qualifier, resourceId));
    }
}
