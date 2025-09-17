package com.omgservers.omgservice.event;

import com.omgservers.omgservice.job.JobExecutor;
import com.omgservers.omgservice.job.JobQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@ApplicationScoped
public class EventProcessor implements JobExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventProcessor.class);
    private static final String FEATURE_FLAG = "omgservice.event.processor.enabled";

    final EventProcessor thisProcessor;
    final EventHandlers eventHandlers;
    final boolean enabled;

    public EventProcessor(final EventProcessor thisProcessor,
                          final EventHandlers eventHandlers,
                          final @ConfigProperty(name = FEATURE_FLAG) boolean enabled) {
        this.thisProcessor = thisProcessor;
        this.eventHandlers = eventHandlers;
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

        final var events = thisProcessor.listEvents();
        if (!events.isEmpty()) {
            LOGGER.info("Processing {} events", events.size());
            events.forEach(this::process);
        }
    }

    public void process(final Event event) {
        try {
            eventHandlers.handle(event);
            markEvent(event.id, false);
        } catch (Exception e) {
            LOGGER.error("Event {} failed. {}", event.id, e.getMessage(), e);
            markEvent(event.id, true);
        }
    }

    @Transactional
    List<Event> listEvents() {
        return Event.listNotDeleted(16);
    }

    @Transactional
    void markEvent(final Long id, final boolean failed) {
        Event.deleteAndMark(id, failed);
    }
}
