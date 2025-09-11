package com.omgservers.omgservice.errors;

import com.omgservers.omgservice.stage.StageStatus;

public class StageStatusMismatch extends ResourceConflict {

    public StageStatusMismatch(final Long stageId,
                               final StageStatus current,
                               final StageStatus required) {
        super("Stage %d has invalid status. Current: %s, required: %s"
                .formatted(stageId, current, required));
    }
}