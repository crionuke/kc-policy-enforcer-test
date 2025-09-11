package com.omgservers.omgservice.errors;

import com.omgservers.omgservice.project.ProjectStatus;

public class ProjectStatusMismatch extends ResourceConflict {

    public ProjectStatusMismatch(final Long projectId,
                                 final ProjectStatus current,
                                 final ProjectStatus required) {
        super("Project %d has invalid status. Current: %s, required: %s"
                .formatted(projectId, current, required));
    }
}