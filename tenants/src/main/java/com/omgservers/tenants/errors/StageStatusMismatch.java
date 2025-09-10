package com.omgservers.tenants.errors;

import com.omgservers.tenants.stage.StageStatus;

public class StageStatusMismatch extends ResourceConflict {

    public StageStatusMismatch(final Long stageId,
                               final StageStatus current,
                               final StageStatus required) {
        super("Stage %d has invalid status. Current: %s, required: %s"
                .formatted(stageId, current, required));
    }
}