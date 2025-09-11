package com.omgservers.tenants.scheduler;

public interface JobExecutor {
    JobQualifier getQualifier();

    void execute(Long resourceId);
}
