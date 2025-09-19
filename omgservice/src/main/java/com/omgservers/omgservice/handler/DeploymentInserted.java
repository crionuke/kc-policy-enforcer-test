package com.omgservers.omgservice.handler;

import com.omgservers.omgservice.deployment.Deployment;
import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DeploymentInserted implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentInserted.class);

    final DeploymentInserted thisHandler;

    public DeploymentInserted(final DeploymentInserted thisHandler) {
        this.thisHandler = thisHandler;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.DEPLOYMENT_INSERTED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var deployment = Deployment.findByIdRequired(resourceId);
        LOGGER.info("Creating {}", deployment);

        thisHandler.finish(resourceId);
        LOGGER.info("{} created successfully", deployment);
    }

    @Transactional
    public void finish(final Long deploymentId) {
        final var version = Deployment.findByIdLocked(deploymentId);
        version.finishCreation();
    }
}
