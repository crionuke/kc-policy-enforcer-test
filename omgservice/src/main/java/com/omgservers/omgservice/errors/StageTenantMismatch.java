package com.omgservers.omgservice.errors;

public class StageTenantMismatch extends ResourceConflict {

    public StageTenantMismatch(final Long stageId,
                               final Long current,
                               final Long required) {
        super("Stage %d tenant mismatch. Current: %d, required: %d"
                .formatted(stageId, current, required));
    }
}