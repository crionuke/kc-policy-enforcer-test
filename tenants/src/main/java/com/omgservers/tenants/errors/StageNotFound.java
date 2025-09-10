package com.omgservers.tenants.errors;

public class StageNotFound extends ResourceNotFound {

    public StageNotFound(final Long stageId) {
        super("Stage %d not found"
                .formatted(stageId));
    }
}
