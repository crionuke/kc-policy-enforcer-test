package com.omgservers.omgservice.deployment;

import jakarta.inject.Provider;
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

    final DeploymentService deploymentService;
    final Provider<String> subClaim;

    public DeploymentResource(final DeploymentService deploymentService,
                              final @Claim(standard = Claims.sub) Provider<String> subClaim) {
        this.deploymentService = deploymentService;
        this.subClaim = subClaim;
    }

    @GET
    @Path("/stage/{stageId}/deployment/{id}")
    public DeploymentProjection getById(@PathParam("stageId") @NotNull final Long stageId,
                              @PathParam("id") @NotNull final Long id) {
        return deploymentService.getById(stageId, id)
                .toProjection();
    }

    @POST
    @ResponseStatus(201)
    @Path("/stage/{stageId}/deployment")
    @Consumes(MediaType.APPLICATION_JSON)
    public DeploymentProjection create(@PathParam("stageId") @NotNull final Long stageId,
                             @NotNull @Valid final NewDeployment newDeployment) {
        return deploymentService.create(stageId, newDeployment, subClaim.get())
                .toProjection();
    }
}
