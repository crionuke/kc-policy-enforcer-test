package com.omgservers.omgservice.errors;

import com.omgservers.omgservice.project.ProjectStatus;

public class DeploymentStatusMismatch extends ResourceConflict {

    public DeploymentStatusMismatch(final Long deployment,
                                    final ProjectStatus current,
                                    final ProjectStatus required) {
        super("Deployment %d has invalid status. Current: %s, required: %s"
                .formatted(deployment, current, required));
    }
}