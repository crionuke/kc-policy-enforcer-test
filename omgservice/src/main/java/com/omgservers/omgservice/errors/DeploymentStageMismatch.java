package com.omgservers.omgservice.errors;

public class DeploymentStageMismatch extends ResourceConflict {

    public DeploymentStageMismatch(final Long deploymentId,
                                   final Long current,
                                   final Long required) {
        super("Deployment %d stage mismatch. Current: %d, required: %d"
                .formatted(deploymentId, current, required));
    }
}