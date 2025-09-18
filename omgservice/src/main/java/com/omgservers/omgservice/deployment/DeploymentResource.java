package com.omgservers.omgservice.deployment;

import com.omgservers.omgservice.event.EventQualifier;
import com.omgservers.omgservice.event.EventService;
import com.omgservers.omgservice.stage.Stage;
import com.omgservers.omgservice.version.Version;
import jakarta.inject.Provider;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.jboss.resteasy.reactive.ResponseStatus;

@Path("/v1")
@Produces(MediaType.APPLICATION_JSON)
public class DeploymentResource {

    final EventService eventService;
    final Provider<String> subClaim;

    public DeploymentResource(final EventService eventService,
                              final @Claim(standard = Claims.sub) Provider<String> subClaim) {
        this.eventService = eventService;
        this.subClaim = subClaim;
    }

    @GET
    @Path("/stage/{stageId}/deployment/{id}")
    public Deployment getById(@PathParam("stageId") @NotNull final Long stageId,
                              @PathParam("id") @NotNull final Long id) {
        final var deployment = Deployment.findByIdRequired(id);
        deployment.ensureStage(stageId);
        return deployment;
    }

    @GET
    @Path("/project/{projectId}/deployment")
    public Deployments getByProjectId(@PathParam("projectId") @NotNull final Long projectId) {
        final var list = Deployment.listByProjectId(projectId);
        final var deployments = new Deployments();
        deployments.size = list.size();
        deployments.list = list;
        return deployments;
    }

    @POST
    @Transactional
    @ResponseStatus(201)
    @Path("/stage/{stageId}/deployment")
    @Consumes(MediaType.APPLICATION_JSON)
    public Deployment create(@PathParam("stageId") @NotNull final Long stageId,
                             @NotNull @Valid final NewDeployment newDeployment) {
        final var stage = Stage.findByIdRequired(stageId);
        stage.ensureCreatedStatus();

        final var versionId = newDeployment.versionId;
        final var version = Version.findByIdRequired(versionId);
        version.ensureTenant(stage.tenant.id);
        version.ensureCreatedStatus();

        final var deployment = new Deployment();
        deployment.createdBy = subClaim.get();
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
