package com.omgservers.omgservice.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.enterprise.inject.Instance;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class EventService {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    public final Map<EventQualifier, EventHandler> handlers;

    public EventService(final Instance<EventHandler> beans) {
        handlers = new ConcurrentHashMap<>();
        beans.stream().forEach(handler -> {
            final var qualifier = handler.getQualifier();
            handlers.put(qualifier, handler);
        });

        log.info("Registered event handlers, {}", handlers.keySet());
    }

    @Transactional
    public List<Event> listForHandling() {
        return Event.listNotDeleted(16);
    }

    @Transactional
    public void deleteAndMark(final Event event, final boolean failed) {
        Event.deleteAndMark(event.id, failed);
    }

    @Transactional
    public void create(final EventQualifier qualifier, final Long resourceId) {
        final var event = new Event();
        event.qualifier = qualifier;
        event.resourceId = resourceId;
        event.failed = false;
        event.persist();
    }

    public void handle(final Event event) {
        final var qualifier = event.qualifier;
        final var handler = handlers.get(qualifier);
        if (Objects.nonNull(handler)) {
            handler.handle(event.resourceId);
        } else {
            log.error("No handler found for {}", qualifier);
        }
    }
}