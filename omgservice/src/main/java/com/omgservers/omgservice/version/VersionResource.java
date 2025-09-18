package com.omgservers.omgservice.version;

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
public class VersionResource {

    final VersionService versionService;
    final Provider<String> subClaim;

    public VersionResource(final VersionService versionService,
                           final @Claim(standard = Claims.sub) Provider<String> subClaim) {
        this.versionService = versionService;
        this.subClaim = subClaim;
    }

    @GET
    @Path("/projects/{projectId}/versions/{id}")
    public VersionProjection getById(@PathParam("projectId") @NotNull final Long projectId,
                                     @PathParam("id") @NotNull final Long id) {
        return versionService.getById(projectId, id)
                .toProjection();
    }

    @POST
    @ResponseStatus(201)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/projects/{projectId}/versions")
    public VersionProjection create(@PathParam("projectId") @NotNull final Long projectId,
                                    @NotNull @Valid final NewVersion newVersion) {
        return versionService.create(projectId, newVersion, subClaim.get())
                .toProjection();
    }
}