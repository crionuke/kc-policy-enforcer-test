package com.omgservers.omgservice.event;

import com.omgservers.omgservice.job.JobExecutor;
import com.omgservers.omgservice.job.JobQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventProcessor implements JobExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventProcessor.class);
    private static final String FEATURE_FLAG = "omgservice.event.processor.enabled";

    final EventHandlers eventHandlers;
    final EventService eventService;
    final boolean enabled;

    public EventProcessor(final EventHandlers eventHandlers,
                          final EventService eventService,
                          final @ConfigProperty(name = FEATURE_FLAG) boolean enabled) {
        this.eventHandlers = eventHandlers;
        this.eventService = eventService;
        this.enabled = enabled;

        if (!enabled) {
            LOGGER.info("Disabled by {} flag", FEATURE_FLAG);
        }
    }

    @Override
    public JobQualifier getQualifier() {
        return JobQualifier.EVENT_PROCESSOR;
    }

    @Override
    public void execute(final Long resourceId) {
        if (!enabled) {
            return;
        }

        final var eventsForHandling = eventService.listForHandling();
        if (!eventsForHandling.isEmpty()) {
            LOGGER.info("Handling {} events", eventsForHandling.size());
            eventsForHandling.forEach(event -> {
                try {
                    eventHandlers.handle(event);
                    eventService.deleteAndMark(event, false);
                } catch (Exception e) {
                    LOGGER.error("Event {} failed. {}", event.id, e.getMessage(), e);
                    eventService.deleteAndMark(event, true);
                }
            });
        }
    }
}
