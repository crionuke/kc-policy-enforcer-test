package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.event.EventHandler;
import com.omgservers.omgservice.event.EventQualifier;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class DeploymentCreatedHandler implements EventHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeploymentCreatedHandler.class);

    final DeploymentCreatedHandler thisHandler;

    public DeploymentCreatedHandler(final DeploymentCreatedHandler thisHandler) {
        this.thisHandler = thisHandler;
    }

    @Override
    public EventQualifier getQualifier() {
        return EventQualifier.DEPLOYMENT_CREATED;
    }

    @Override
    public void handle(final Long resourceId) {
        final var deployment = Deployment.findByIdRequired(resourceId);
        LOGGER.info("Creating deployment {}", deployment);

        thisHandler.finish(resourceId);
        LOGGER.info("Deployment {} created successfully", deployment);
    }

    @Transactional
    public void finish(final Long deploymentId) {
        final var version = Deployment.findByIdLocked(deploymentId);
        version.finishCreation();
    }
}
