package com.omgservers.tenants.event;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@ApplicationScoped
public class EventJobScheduler {
    private static final Logger log = LoggerFactory.getLogger(EventJobScheduler.class);

    static final String JOB_IDENTITY = "event-job";
    static final String TRIGGER_IDENTITY = "event-trigger";

    final Scheduler scheduler;

    public EventJobScheduler(final Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    void onStart(@Observes StartupEvent event) throws SchedulerException {
        final var job = JobBuilder.newJob(EventJob.class)
                .withIdentity(JOB_IDENTITY)
                .storeDurably()
                .build();

        final var trigger = TriggerBuilder.newTrigger()
                .withIdentity(TRIGGER_IDENTITY)
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(1)
                        .withMisfireHandlingInstructionNextWithExistingCount()
                        .repeatForever())
                .build();

        scheduler.scheduleJob(job, Set.of(trigger), true);
    }

    void performTask() {
    }

    @DisallowConcurrentExecution
    public static class EventJob implements Job {

        @Inject
        EventJobScheduler eventJobScheduler;

        public void execute(JobExecutionContext context) throws JobExecutionException {
            eventJobScheduler.performTask();
        }
    }
}
