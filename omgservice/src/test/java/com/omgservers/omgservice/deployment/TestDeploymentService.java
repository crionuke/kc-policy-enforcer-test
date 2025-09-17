package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.TestEventService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class TestDeploymentService {

    @Inject
    DeploymentResourceClient deploymentResourceClient;

    @Inject
    TestEventService testEventService;

    public Deployment createDeployment(final Long stageId,
                                       final NewDeployment newDeployment,
                                       final boolean process,
                                       final String token) {
        final var deployment = deploymentResourceClient.createCheck201(stageId, newDeployment, token);
        if (process) {
            testEventService.process(EventQualifier.DEPLOYMENT_CREATED, deployment.id);
        }

        return deployment;
    }

    public Deployment createDeployment(final Long stageId,
                                       final Long versionId,
                                       final boolean process,
                                       final String token) {
        final var newDeployment = new NewDeployment();
        newDeployment.versionId = versionId;
        return createDeployment(stageId, newDeployment, process, token);
    }
}
