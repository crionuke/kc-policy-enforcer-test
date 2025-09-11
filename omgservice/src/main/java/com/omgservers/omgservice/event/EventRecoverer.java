package com.omgservers.omgservice.event;

import com.omgservers.omgservice.job.JobExecutor;
import com.omgservers.omgservice.job.JobQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class EventRecoverer implements JobExecutor {
    private static final Logger log = LoggerFactory.getLogger(EventRecoverer.class);

    @Override
    public JobQualifier getQualifier() {
        return JobQualifier.EVENT_RECOVERER;
    }

    @Override
    public void execute(final Long resourceId) {
    }
}
