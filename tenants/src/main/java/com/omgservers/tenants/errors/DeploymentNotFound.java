package com.omgservers.tenants.errors;

public class DeploymentNotFound extends ResourceNotFound {

    public DeploymentNotFound(final Long deploymentId) {
        super("Deployment %d not found"
                .formatted(deploymentId));
    }
}