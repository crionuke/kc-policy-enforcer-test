package com.omgservers.tenants.scheduler.executors;

import com.omgservers.tenants.event.EventService;
import com.omgservers.tenants.scheduler.JobExecutor;
import com.omgservers.tenants.scheduler.JobQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventProcessorExecutor implements JobExecutor {
    private static final Logger log = LoggerFactory.getLogger(EventProcessorExecutor.class);

    final EventService eventService;

    public EventProcessorExecutor(final EventService eventService) {
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
                    eventService.handle(event);
                    eventService.deleteAndMark(event, false);
                } catch (Exception e) {
                    log.error("Event {} failed, {}", event.id, e.getMessage(), e);
                    eventService.deleteAndMark(event, true);
                }
            });
        }
    }
}
