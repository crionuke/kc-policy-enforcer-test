package com.omgservers.omgservice.event;

import com.omgservers.omgservice.job.JobQualifier;
import com.omgservers.omgservice.job.JobService;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventStartup {
    private static final Logger log = LoggerFactory.getLogger(EventStartup.class);

    final JobService jobService;

    public EventStartup(final JobService jobService) {
        this.jobService = jobService;
    }

    void onStart(@Observes StartupEvent event) throws SchedulerException {
        jobService.schedule(JobQualifier.EVENT_PROCESSOR);
        jobService.schedule(JobQualifier.EVENT_RECOVERER);
    }
}
