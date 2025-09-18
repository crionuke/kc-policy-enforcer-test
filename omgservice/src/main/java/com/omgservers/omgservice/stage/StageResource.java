package com.omgservers.omgservice.stage;

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
public class StageResource {

    final StageService stageService;
    final Provider<String> subClaim;

    public StageResource(final StageService stageService,
                         final @Claim(standard = Claims.sub) Provider<String> subClaim) {
        this.stageService = stageService;
        this.subClaim = subClaim;
    }

    @GET
    @Path("/stage/{id}")
    public StageProjection getById(@NotNull final Long id) {
        return stageService.getById(id)
                .toProjection();
    }

    @POST
    @ResponseStatus(201)
    @Path("/tenant/{tenantId}/stage")
    @Consumes(MediaType.APPLICATION_JSON)
    public StageProjection create(@PathParam("tenantId") @NotNull final Long tenantId,
                        @NotNull @Valid final NewStage newStage) {
        return stageService.create(tenantId, newStage, subClaim.get())
                .toProjection();
    }
}
