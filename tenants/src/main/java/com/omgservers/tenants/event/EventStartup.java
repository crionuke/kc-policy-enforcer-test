package com.omgservers.tenants.event;

import com.omgservers.tenants.scheduler.JobQualifier;
import com.omgservers.tenants.scheduler.SchedulerService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventStartup {
    private static final Logger log = LoggerFactory.getLogger(EventStartup.class);

    final SchedulerService schedulerService;

    public EventStartup(final SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    void onStart(@Observes StartupEvent event) throws SchedulerException {
        schedulerService.schedule(JobQualifier.EVENT_PROCESSOR);
        schedulerService.schedule(JobQualifier.EVENT_RECOVERER);
    }
}
