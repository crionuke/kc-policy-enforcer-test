package com.omgservers.tenants.errors;

public class ProjectNotFound extends ResourceNotFound {

    public ProjectNotFound(final Long projectId) {
        super("Project %d not found"
                .formatted(projectId));
    }
}
