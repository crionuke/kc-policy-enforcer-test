package com.omgservers.tenants.scheduler;

import io.quarkus.arc.Arc;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;
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

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
public class SchedulerService {
    private static final Logger log = LoggerFactory.getLogger(SchedulerService.class);

    private static final String RESOURCE_ID_JOB_PROPERTY = "resourceId";

    final Map<JobQualifier, JobExecutor> executors;
    final Scheduler scheduler;

    public SchedulerService(final Scheduler scheduler,
                            final Instance<JobExecutor> beans) {
        this.scheduler = scheduler;

        executors = new ConcurrentHashMap<>();
        beans.stream().forEach(executor -> {
            final var qualifier = executor.getQualifier();
            executors.put(qualifier, executor);
        });

        log.info("Registered job executors, {}", executors.keySet());
    }

    public void schedule(final JobQualifier qualifier,
                         final Long resourceId) throws SchedulerException {
        final var identity = qualifier.identity;
        final var group = qualifier.group;

        final var jobDetail = JobBuilder.newJob(ScheduledJob.class)
                .withIdentity(identity, group)
                .usingJobData(RESOURCE_ID_JOB_PROPERTY, resourceId.toString())
                .build();

        final var jobTrigger = TriggerBuilder.newTrigger()
                .withIdentity(identity)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(qualifier.interval)
                        .withMisfireHandlingInstructionNextWithExistingCount()
                        .repeatForever())
                .build();

        scheduler.scheduleJob(jobDetail, Set.of(jobTrigger), true);
        log.info("Job {} scheduled", identity);
    }

    public void schedule(final JobQualifier qualifier) throws SchedulerException {
        schedule(qualifier, 0L);
    }

    void executeJob(final JobQualifier qualifier, final Long resourceId) {
        final var executor = executors.get(qualifier);
        if (Objects.nonNull(executor)) {
            executor.execute(resourceId);
        } else {
            log.error("No executor found for {} job", qualifier);
        }
    }

    @DisallowConcurrentExecution
    public static class ScheduledJob implements Job {

        public void execute(JobExecutionContext context) throws JobExecutionException {
            final var identity = context.getJobDetail().getKey().getName();
            final var jobQualifier = JobQualifier.fromString(identity);
            final var resourceIdString = context.getJobDetail().getJobDataMap().getString(RESOURCE_ID_JOB_PROPERTY);
            final var resourceId = Long.valueOf(resourceIdString);

            try (final var schedulerServiceInstance = Arc.container().instance(SchedulerService.class)) {
                final var schedulerService = schedulerServiceInstance.get();
                schedulerService.executeJob(jobQualifier, resourceId);
            }
        }
    }
}


