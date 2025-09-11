package com.omgservers.omgservice.errors;

public class DeploymentTenantMismatch extends ResourceConflict {

    public DeploymentTenantMismatch(final Long deploymentId,
                                    final Long current,
                                    final Long required) {
        super("Deployment %d tenant mismatch. Current: %d, required: %d"
                .formatted(deploymentId, current, required));
    }
}