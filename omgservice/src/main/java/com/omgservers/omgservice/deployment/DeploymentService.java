package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
import com.omgservers.omgservice.stage.Stage;
import com.omgservers.omgservice.version.Version;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@Transactional
@ApplicationScoped
public class DeploymentService {

    final EventService eventService;

    public DeploymentService(final EventService eventService) {
        this.eventService = eventService;
    }

    public Deployment getById(final Long stageId, final Long id) {
        final var deployment = Deployment.findByIdRequired(id);
        deployment.ensureStage(stageId);
        return deployment;
    }

    public Deployment create(final Long stageId,
                             final NewDeployment newDeployment,
                             final String createdBy) {
        final var stage = Stage.findByIdRequired(stageId);
        stage.ensureCreatedStatus();

        final var versionId = newDeployment.versionId;
        final var version = Version.findByIdRequired(versionId);
        version.ensureTenant(stage.tenant.id);
        version.ensureCreatedStatus();

        final var deployment = new Deployment();
        deployment.createdBy = createdBy;
        deployment.stage = stage;
        deployment.version = version;
        deployment.status = DeploymentStatus.CREATING;
        deployment.config = new DeploymentConfig();
        deployment.config.version = DeploymentConfigVersion.V1;
        deployment.persist();

        eventService.create(EventQualifier.DEPLOYMENT_CREATED, deployment.id);

        return deployment;
    }
}
