package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DeploymentCreatedHandler implements EventHandler {

    final DeploymentService deploymentService;

    public DeploymentCreatedHandler(final DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.DEPLOYMENT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        deploymentService.switchStateFromCreatingToCreated(resourceId);
    }
}
