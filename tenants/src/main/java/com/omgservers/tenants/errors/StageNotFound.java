package com.omgservers.tenants.errors;

import java.util.UUID;

public class StageNotFound extends ResourceNotFound {

    public StageNotFound(final UUID stageId) {
        super("Stage %s not found"
                .formatted(stageId));
    }
}
