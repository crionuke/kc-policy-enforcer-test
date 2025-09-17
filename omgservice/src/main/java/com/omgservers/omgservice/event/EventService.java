package com.omgservers.omgservice.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Transactional
@ApplicationScoped
public class EventService {

    public void create(final EventQualifier qualifier, final Long resourceId) {
        final var event = new Event();
        event.qualifier = qualifier;
        event.resourceId = resourceId;
        event.failed = false;
        event.persist();
    }
}