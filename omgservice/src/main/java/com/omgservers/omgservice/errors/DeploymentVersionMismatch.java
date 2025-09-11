package com.omgservers.omgservice.errors;

public class DeploymentVersionMismatch extends ResourceConflict {

    public DeploymentVersionMismatch(final Long deploymentId,
                                     final Long current,
                                     final Long required) {
        super("Deployment %d version mismatch. Current: %d, required: %d"
                .formatted(deploymentId, current, required));
    }
}