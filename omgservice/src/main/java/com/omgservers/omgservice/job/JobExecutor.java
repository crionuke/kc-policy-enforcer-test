package com.omgservers.omgservice.job;

public interface JobExecutor {
    JobQualifier getQualifier();

    void execute(Long resourceId);
}
