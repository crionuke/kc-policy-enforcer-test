package com.omgservers.omgservice.project;

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
public class ProjectResource {

    final ProjectService projectService;
    final Provider<String> subClaim;

    public ProjectResource(final ProjectService projectService,
                           final @Claim(standard = Claims.sub) Provider<String> subClaim) {
        this.projectService = projectService;
        this.subClaim = subClaim;
    }

    @GET
    @Path("/project/{id}")
    public ProjectProjection getById(@NotNull final Long id) {
        return projectService.getById(id)
                .toProjection();
    }

    @POST
    @ResponseStatus(201)
    @Path("/tenant/{tenantId}/project")
    @Consumes(MediaType.APPLICATION_JSON)
    public ProjectProjection create(@PathParam("tenantId") @NotNull final Long tenantId,
                                    @NotNull @Valid final NewProject newProject) {
        return projectService.create(tenantId, newProject, subClaim.get())
                .toProjection();
    }
}
