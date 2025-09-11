package com.omgservers.tenants.scheduler.executors;

import com.omgservers.tenants.scheduler.JobExecutor;
import com.omgservers.tenants.scheduler.JobQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventRecovererExecutor implements JobExecutor {
    private static final Logger log = LoggerFactory.getLogger(EventRecovererExecutor.class);

    @Override
    public JobQualifier getQualifier() {
        return JobQualifier.EVENT_RECOVERER;
    }

    @Override
    public void execute(final Long resourceId) {
    }
}
