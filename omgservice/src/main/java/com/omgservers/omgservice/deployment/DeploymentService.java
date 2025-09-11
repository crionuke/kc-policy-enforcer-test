package com.omgservers.omgservice.deployment;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Transactional
@ApplicationScoped
public class DeploymentService {

    public boolean switchStateFromCreatingToCreated(final Long deploymentId) {
        final var deployment = Deployment.findByIdRequired(deploymentId);
        if (deployment.status == DeploymentStatus.CREATING) {
            deployment.status = DeploymentStatus.CREATED;
            return true;
        } else {
            return false;
        }
    }
}
