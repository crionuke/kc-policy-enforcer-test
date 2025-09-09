package com.omgservers.tenants.errors;

import com.omgservers.tenants.project.ProjectStatus;

import java.util.UUID;

public class ProjectStatusMismatch extends ResourceConflict {

    public ProjectStatusMismatch(final UUID projectId,
                                 final ProjectStatus current,
                                 final ProjectStatus required) {
        super("Project %s has invalid status. Current: %s, required: %s"
                .formatted(projectId, current, required));
    }
}