package com.omgservers.tenants.errors;

import java.util.UUID;

public class ProjectNotFound extends ResourceNotFound {

    public ProjectNotFound(final UUID projectId) {
        super("Stage %s not found"
                .formatted(projectId));
    }
}
