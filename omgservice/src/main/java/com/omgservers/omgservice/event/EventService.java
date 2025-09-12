package com.omgservers.omgservice.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

import java.util.List;

@Transactional
@ApplicationScoped
public class EventService {

    public List<Event> listForHandling() {
        return Event.listNotDeleted(16);
    }

    public void deleteAndMark(final Event event, final boolean failed) {
        Event.deleteAndMark(event.id, failed);
    }

    public void create(final EventQualifier qualifier, final Long resourceId) {
        final var event = new Event();
        event.qualifier = qualifier;
        event.resourceId = resourceId;
        event.failed = false;
        event.persist();
    }
}