package com.omgservers.omgservice.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class EventHandlers {
    private static final Logger log = LoggerFactory.getLogger(EventService.class);

    public final Map<EventQualifier, EventHandler> handlers;

    public EventHandlers(final Instance<EventHandler> beans) {
        handlers = new ConcurrentHashMap<>();
        beans.stream().forEach(handler -> {
            final var qualifier = handler.getQualifier();
            handlers.put(qualifier, handler);
        });

        log.info("Registered event handlers, {}", handlers.keySet());
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
