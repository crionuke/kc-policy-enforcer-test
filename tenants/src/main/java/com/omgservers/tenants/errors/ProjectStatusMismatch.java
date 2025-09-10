package com.omgservers.tenants.errors;

import com.omgservers.tenants.project.ProjectStatus;

public class ProjectStatusMismatch extends ResourceConflict {

    public ProjectStatusMismatch(final Long projectId,
                                 final ProjectStatus current,
                                 final ProjectStatus required) {
        super("Project %d has invalid status. Current: %s, required: %s"
                .formatted(projectId, current, required));
    }
}