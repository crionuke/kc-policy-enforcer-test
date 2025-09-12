package com.omgservers.omgservice.event;

import com.omgservers.omgservice.job.JobExecutor;
import com.omgservers.omgservice.job.JobQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventProcessor implements JobExecutor {
    private static final Logger log = LoggerFactory.getLogger(EventProcessor.class);

    final EventHandlers eventHandlers;
    final EventService eventService;

    public EventProcessor(final EventHandlers eventHandlers,
                          final EventService eventService) {
        this.eventHandlers = eventHandlers;
        this.eventService = eventService;
    }

    @Override
    public JobQualifier getQualifier() {
        return JobQualifier.EVENT_PROCESSOR;
    }

    @Override
    public void execute(final Long resourceId) {
        final var eventsForHandling = eventService.listForHandling();
        if (!eventsForHandling.isEmpty()) {
            log.info("Handling {} events", eventsForHandling.size());
            eventsForHandling.forEach(event -> {
                try {
                    eventHandlers.handle(event);
                    eventService.deleteAndMark(event, false);
                } catch (Exception e) {
                    log.error("Event {} failed, {}", event.id, e.getMessage(), e);
                    eventService.deleteAndMark(event, true);
                }
            });
        }
    }
}
